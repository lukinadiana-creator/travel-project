package io.github.lukinadiana_creator.travel_planner.hotel.hotelbeds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HotelsAvailabilityDto(
    List<AvailableHotelDto> hotels
) {}
