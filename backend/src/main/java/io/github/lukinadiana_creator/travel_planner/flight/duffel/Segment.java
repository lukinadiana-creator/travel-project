package io.github.lukinadiana_creator.travel_planner.flight.duffel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record Segment(
        @JsonProperty("departing_at")
        LocalDateTime departingAt,
        @JsonProperty("arriving_at")
        LocalDateTime arrivingAt,
        @JsonProperty("operating_carrier")
        Carrier operatingCarrier
) {}
