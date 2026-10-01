package io.github.lukinadiana_creator.travel_planner.hotel;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RoomDto(
        String roomType,
        String meal,
        int numberOfPerson,
        LocalDate arrivalDate,
        LocalDate departureDate,
        BigDecimal price
) {}
