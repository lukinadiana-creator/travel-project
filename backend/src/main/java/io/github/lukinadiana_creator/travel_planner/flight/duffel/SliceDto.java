package io.github.lukinadiana_creator.travel_planner.flight.duffel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record SliceDto(
        @JsonProperty("departure_date")
        LocalDate date,
        String destination,
        String origin
) {}
