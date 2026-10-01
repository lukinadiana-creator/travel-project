package io.github.lukinadiana_creator.travel_planner.hotel;

import io.github.lukinadiana_creator.travel_planner.currency.CurrencyService;
import io.github.lukinadiana_creator.travel_planner.hotel.hotelbeds.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class HotelMapper {
    private final CurrencyService currencyService;
    private static final String HOTELBEDS_PHOTO_URL =
            "https://photos.hotelbeds.com/giata/bigger/";

    public HotelMapper(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    public Hotel toEntity(HotelContentDto dto, String destinationCode) {
        Hotel hotel = new Hotel();

        hotel.setHotelbedsCode(dto.code());
        hotel.setDestinationCode(destinationCode);
        hotel.setName(dto.name() != null ? dto.name().content() : null);
        hotel.setRating(dto.ranking() != null ? dto.ranking() : 0);
        hotel.setLocation(dto.city() != null ? dto.city().content() : null);
        hotel.setDescription(dto.description() != null ? dto.description().content() : null);
        hotel.setLatitude(dto.coordinates() != null ? dto.coordinates().latitude() : 0);
        hotel.setLongitude(dto.coordinates() != null ? dto.coordinates().longitude() : 0);
        hotel.setImageUrls(dto.images() != null ? dto.images().stream()
                .filter(image -> "COM".equals(image.imageTypeCode()))
                .map(ImageDto::path).limit(5).toList() : null);

        return hotel;
    }

    public HotelDto toDto(Hotel hotel, AvailableHotelDto availableHotel, LocalDate checkIn, LocalDate checkOut) {
        if (hotel == null) return null;

        List<RoomDto> rooms = availableHotel.rooms()
                .stream()
                .flatMap(room -> room.rates()
                        .stream().map(rate -> toRoomDto(
                                room,
                                rate,
                                availableHotel.currency(),
                                checkIn,
                                checkOut
                        )))
                .toList();

        return new HotelDto(
                hotel.getHotelbedsCode(),
                hotel.getName(),
                hotel.getRating(),
                hotel.getLocation(),
                hotel.getDescription(),
                hotel.getLatitude(),
                hotel.getLongitude(),
                hotel.getImageUrls().stream()
                        .map(img -> HOTELBEDS_PHOTO_URL+img)
                        .toList(),
                rooms
        );
    }

    public RoomDto toRoomDto(AvailableRoomDto room, RateDto rate, String currency, LocalDate checkIn, LocalDate checkOut) {
        BigDecimal amountRub = currencyService.convertToRub(rate.net(), currency);

        return new RoomDto(
                room.name(),
                rate.boardName(),
                rate.adults(),
                checkIn,
                checkOut,
                amountRub
        );
    }

    public Room toRoomEntity(RoomDto dto) {
        Room room = new Room();

        room.setRoomType(dto.roomType());
        room.setMeal(dto.meal());
        room.setNumberOfPerson(dto.numberOfPerson());
        room.setArrivalDate(dto.arrivalDate());
        room.setDepartureDate(dto.departureDate());
        room.setPrice(dto.price());

        return room;
    }
}
