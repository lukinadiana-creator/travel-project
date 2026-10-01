package io.github.lukinadiana_creator.travel_planner.restaurant;

import java.math.BigDecimal;

public record RestaurantDto(
        Long id,
        String foodType, //или через enum
        String name,
        double rating,
        String location,
        Double latitude,
        Double longitude,
        BigDecimal averageBill,
        String imageUrl
) {}