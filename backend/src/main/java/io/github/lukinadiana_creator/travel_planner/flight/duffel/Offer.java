package io.github.lukinadiana_creator.travel_planner.flight.duffel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record Offer(
        @JsonProperty("total_amount")
        BigDecimal totalAmount,
        @JsonProperty("total_currency")
        String totalCurrency,
        List<Slice> slices
) {}
