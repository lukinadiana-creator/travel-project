package io.github.lukinadiana_creator.travel_planner.flight;

import java.time.LocalDateTime;

public record FlightDto(
        String companyName,
        LocalDateTime arrivalDate,
        String cityTo,
        LocalDateTime departureDate,
        String cityFrom
) {}
