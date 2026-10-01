package io.github.lukinadiana_creator.travel_planner.tour;

import io.github.lukinadiana_creator.travel_planner.flight.FlightPairDto;
import io.github.lukinadiana_creator.travel_planner.hotel.HotelDto;

import java.math.BigDecimal;
import java.util.List;

public record TourDto(
        BigDecimal minTotalPrice,
        HotelDto hotel,
        List<FlightPairDto> flights
) {}
