package io.github.lukinadiana_creator.travel_planner.flight;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CityInfo(
        String iata,
        @JsonProperty("name_en")
        String nameEn,
        String country
) {}
