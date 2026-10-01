package io.github.lukinadiana_creator.travel_planner.favorite;

import io.github.lukinadiana_creator.travel_planner.restaurant.Restaurant;

import java.math.BigDecimal;

public record FavoriteRestaurantDto(
        Long id,
        String foodType, //или через enum
        String name,
        double rating,
        String location,
        BigDecimal averageBill,
        String imageUrl
) {}
