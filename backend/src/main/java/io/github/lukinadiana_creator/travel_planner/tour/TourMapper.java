package io.github.lukinadiana_creator.travel_planner.tour;

import io.github.lukinadiana_creator.travel_planner.hotel.HotelDto;
import io.github.lukinadiana_creator.travel_planner.hotel.RoomDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;

@Component
public class TourMapper {
    public TourSearchItemDto toSearchItem(HotelDto hotelDto) {
        BigDecimal minPrice = hotelDto.rooms().stream()
                .map(RoomDto::price)
                .min(Comparator.naturalOrder())
                .orElse(null);

        return new TourSearchItemDto(
                hotelDto.hotelbedsCode(),
                hotelDto.name(),
                hotelDto.rating(),
                hotelDto.location(),
                hotelDto.imageUrls().isEmpty() ? null : hotelDto.imageUrls().get(0),
                minPrice
        );
    }
}
