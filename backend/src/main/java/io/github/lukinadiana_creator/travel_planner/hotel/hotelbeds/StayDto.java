package io.github.lukinadiana_creator.travel_planner.hotel.hotelbeds;

import java.time.LocalDate;

public record StayDto(
        LocalDate checkIn,
        LocalDate checkOut
) {}
