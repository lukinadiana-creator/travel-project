package io.github.lukinadiana_creator.travel_planner.google;

public record Money(
        String currencyCode,
        Long units,
        Integer nanos
) {}
