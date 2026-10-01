package io.github.lukinadiana_creator.travel_planner.currency;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record CbResponseDto(
        @JsonProperty("Valute")
        Map<String, ValuteDto> valute
) {}
