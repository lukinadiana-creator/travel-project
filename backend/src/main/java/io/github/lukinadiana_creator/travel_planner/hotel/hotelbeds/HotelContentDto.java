package io.github.lukinadiana_creator.travel_planner.hotel.hotelbeds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HotelContentDto(
        Long code,
        ContentDto name,
        ContentDto description,
        String destinationCode,
        ContentDto city,
        CoordinateDto coordinates,
        Integer ranking,
        List<ImageDto> images
) {}
