package io.github.lukinadiana_creator.travel_planner.favorite;

import io.github.lukinadiana_creator.travel_planner.hotel.Hotel;

public record FavoriteHotelDto(
        Long hotelbedsCode,
        String name,
        int rating,
        String location,
        String imageUrl
) {}
