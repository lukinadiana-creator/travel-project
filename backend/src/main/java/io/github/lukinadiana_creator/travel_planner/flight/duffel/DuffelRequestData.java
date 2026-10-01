package io.github.lukinadiana_creator.travel_planner.flight.duffel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DuffelRequestData(
        @JsonProperty("cabin_class")
        String cabinClass,
        List<SliceDto> slices,
        List<PassengerDto> passengers,
        @JsonProperty("max_connections")
        Integer maxConnections
) {}
