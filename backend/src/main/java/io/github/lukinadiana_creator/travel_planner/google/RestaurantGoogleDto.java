package io.github.lukinadiana_creator.travel_planner.google;

import java.util.List;

public record RestaurantGoogleDto(
        DisplayName primaryTypeDisplayName,
        DisplayName displayName,
        double rating,
        String formattedAddress,
        Location location,
        PriceRange priceRange,
        List<Photo> photos
) { }
