package io.github.lukinadiana_creator.travel_planner.hotel.hotelbeds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RateDto(
        String rateKey,
        BigDecimal net,
        String boardName,
        String boardCode,
        int adults
) {}
