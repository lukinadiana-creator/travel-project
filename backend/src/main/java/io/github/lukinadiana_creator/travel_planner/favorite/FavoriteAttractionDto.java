package io.github.lukinadiana_creator.travel_planner.favorite;

import io.github.lukinadiana_creator.travel_planner.attraction.Attraction;

public record FavoriteAttractionDto(
        Long id,
        String attractionType,
        String name,
        String location,
        String description,
        String imageUrl
) {}
