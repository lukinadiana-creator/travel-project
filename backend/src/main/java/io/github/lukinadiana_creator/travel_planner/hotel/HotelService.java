package io.github.lukinadiana_creator.travel_planner.hotel;

import io.github.lukinadiana_creator.travel_planner.exception.EntityNotFoundException;
import io.github.lukinadiana_creator.travel_planner.exception.ExternalServiceException;
import io.github.lukinadiana_creator.travel_planner.exception.InvalidRequestException;
import io.github.lukinadiana_creator.travel_planner.flight.CityInfo;
import io.github.lukinadiana_creator.travel_planner.hotel.hotelbeds.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class HotelService {
    private static final Logger log = LoggerFactory.getLogger(HotelService.class);
    private final HotelRepository hotelRepository;
    private final WebClient webClientHotelbeds;
    private final HotelbedsSignature hotelbedsSignature;
    private final HotelMapper hotelMapper;
    private final RoomRepository roomRepository;
    private final Map<String, CityInfo> cities;

    @Value("${hotelbeds.api-key}")
    private String apiKey;

    @Value("${hotelbeds.secret}")
    private String secret;

    public HotelService(HotelRepository hotelRepository, WebClient webClientHotelbeds, HotelbedsSignature hotelbedsSignature, HotelMapper hotelMapper, RoomRepository roomRepository) {
        this.hotelRepository = hotelRepository;
        this.webClientHotelbeds = webClientHotelbeds;
        this.hotelbedsSignature = hotelbedsSignature;
        this.hotelMapper = hotelMapper;
        this.roomRepository = roomRepository;
        this.cities = loadCities();
    }

    public List<HotelDto> searchHotels(String city, LocalDate checkIn, LocalDate checkOut, int adults) {
        String destinationCode = findIata(city);

        List<Hotel> hotels = hotelRepository.findByDestinationCode(destinationCode);

        if (hotels.isEmpty()) {
            hotels = loadHotelsFromContentApi(destinationCode);
        }

        if (hotels.isEmpty()) return List.of();

        List<Long> hotelCodes = hotels.stream()
                .map(Hotel::getHotelbedsCode)
                .toList();

        HotelAvailabilityResponse availability = getAvailability(
                hotelCodes,
                checkIn,
                checkOut,
                adults
        );

        if (availability == null || availability.hotels() == null) {
            return List.of();
        }

        Map<Long, Hotel> hotelsByCode = hotels.stream()
                .collect(Collectors.toMap(
                        Hotel::getHotelbedsCode,
                        Function.identity()
                ));

        return availability.hotels().hotels().stream()
                .map(availableHotel -> hotelMapper.toDto(
                        hotelsByCode.get(availableHotel.code()),
                        availableHotel,
                        checkIn,
                        checkOut
                ))
                .filter(Objects::nonNull)
                .toList();
    }

    public HotelDto getHotelWithAvailability(Long hotelbedsCode, LocalDate checkIn, LocalDate checkOut, int adults) {
        Hotel hotel = hotelRepository.findByHotelbedsCode(hotelbedsCode)
                .orElseThrow(() -> new EntityNotFoundException("Отель не найден: " + hotelbedsCode));

        HotelAvailabilityResponse availability = getAvailability(List.of(hotelbedsCode), checkIn, checkOut, adults);

        if (availability == null || availability.hotels() == null || availability.hotels().hotels().isEmpty()) {
            return hotelMapper.toDto(hotel, null, checkIn, checkOut);
        }

        return hotelMapper.toDto(hotel, availability.hotels().hotels().get(0), checkIn, checkOut);
    }

    private List<Hotel> loadHotelsFromContentApi(String destinationCode) {
        String signature = hotelbedsSignature.generate(apiKey, secret);

        HotelContentResponse response;
        try {
            response = webClientHotelbeds.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/hotel-content-api/1.0/hotels")
                            .queryParam("fields", "all")
                            .queryParam("destinationCode", destinationCode)
                            .queryParam("language", "RUS")
                            .queryParam("from", 1)
                            .queryParam("to", 20)
                            .build())
                    .header("Api-key", apiKey)
                    .header("X-Signature", signature)
                    .retrieve()
                    .bodyToMono(HotelContentResponse.class)
                    .block();
        } catch (WebClientResponseException | WebClientRequestException e) {
            throw new ExternalServiceException("Не удалось получить данные об отелях, попробуйте позже", e);
        }

        if (response == null || response.hotels() == null) return List.of();

        List<Hotel> hotels = response.hotels()
                .stream()
                .map(dto -> hotelMapper.toEntity(dto, destinationCode))
                .toList();

        try {
            return hotelRepository.saveAll(hotels);
        } catch (DataAccessException e) {
            log.warn("Не удалось сохранить отели в кеш для {}", destinationCode, e);
            return hotels;
        }
    }

    private HotelAvailabilityResponse getAvailability(List<Long> hotelCodes, LocalDate checkIn, LocalDate checkOut, int adults) {
        AvailabilityRequest request = new AvailabilityRequest(
                new StayDto(
                        checkIn,
                        checkOut
                ),
                List.of(new OccupancyDto(
                        1,
                        adults,
                        0
                )),
                new HotelsRequestDto(hotelCodes)
        );

        String signature = hotelbedsSignature.generate(apiKey, secret);

        try {
            return webClientHotelbeds.post()
                    .uri("/hotel-api/1.0/hotels")
                    .header("Api-key", apiKey)
                    .header("X-Signature", signature)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(HotelAvailabilityResponse.class)
                    .block();
        } catch (WebClientResponseException | WebClientRequestException e) {
            throw new ExternalServiceException("Не удалось проверить доступность номеров, попробуйте позже", e);
        }
    }

    public Room saveRoom(RoomDto dto) {
        try {
            Room entity = hotelMapper.toRoomEntity(dto);
            return roomRepository.save(entity);
        } catch (DataAccessException e) {
            throw new ExternalServiceException("Не удалось сохранить данные о номере", e);
        }
    }

    private Map<String, CityInfo> loadCities() {
        try {
            ClassPathResource resource = new ClassPathResource("data/cities-ru.json");
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(resource.getInputStream(), new TypeReference<Map<String, CityInfo>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить cities-ru.json", e);
        }
    }

    private String findIata(String city) {
        CityInfo cityInfo = cities.get(city.toLowerCase());
        if (cityInfo == null) {
            throw new InvalidRequestException("Город не найден: " + city);
        }

        return cityInfo.iata();
    }
}
