package io.github.lukinadiana_creator.travel_planner.currency;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ValuteDto(
        @JsonProperty("CharCode")
        String charCode,
        @JsonProperty("Nominal")
        int nominal,
        @JsonProperty("Value")
        double value
) {}
