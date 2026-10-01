package io.github.lukinadiana_creator.travel_planner.attraction;

public record AttractionDto(
        Long id,
        String attractionType, //или через enum
        String name,
        String location,
        Double latitude,
        Double longitude,
        String description,
        String imageUrl
) {}