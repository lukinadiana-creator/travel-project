package io.github.lukinadiana_creator.travel_planner.flight;

import io.github.lukinadiana_creator.travel_planner.exception.ExternalServiceException;
import io.github.lukinadiana_creator.travel_planner.exception.InvalidRequestException;
import io.github.lukinadiana_creator.travel_planner.flight.duffel.*;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class FlightService {
    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    private final WebClient webClientDuffel;
    private final Map<String, CityInfo> cities;

    public FlightService(FlightRepository flightRepository, FlightMapper flightMapper, WebClient webClientDuffel) {
        this.flightRepository = flightRepository;
        this.flightMapper = flightMapper;
        this.webClientDuffel = webClientDuffel;
        this.cities = loadCities();
    }

    public List<FlightPairDto> getFlight(String cityFrom, String cityTo, LocalDate departureDate, int nightCount) {
        DuffelRequestDto request = new DuffelRequestDto(new DuffelRequestData(
                "economy",
                List.of(new SliceDto(
                        departureDate,
                        findIata(cityTo),
                        findIata(cityFrom)
                        ),
                        new SliceDto(
                       departureDate.plusDays(nightCount),
                       findIata(cityFrom),
                       findIata(cityTo)
                        )),
                List.of(new PassengerDto("adult")),
                0
        ));

        DuffelResponseDto response;
        try {
            response = webClientDuffel.post()
                    .uri("/air/offer_requests")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(DuffelResponseDto.class)
                    .block();
        } catch (WebClientResponseException | WebClientRequestException e) {
            throw new ExternalServiceException("Не удалось получить данные о перелетах, попробуйте позже", e);
        }

        if (response == null || response.data().offers() == null || response.data().offers().isEmpty()) {
            return List.of();
        }

        return response.data().offers().stream()
                .map(offer -> flightMapper.toFlightPairDto(cityFrom, cityTo, offer))
                .sorted(Comparator.comparing(FlightPairDto::totalPrice))
                .limit(4)
                .toList();

    }

    public Flight saveFlight(FlightPairDto pairDto) {
        try {
            Flight entity = flightMapper.toEntity(pairDto);
            return flightRepository.save(entity);
        } catch (DataAccessException e) {
            throw new ExternalServiceException("Не удалось сохранить перелёт", e);
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
