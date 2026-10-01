package io.github.lukinadiana_creator.travel_planner.tour;

import java.math.BigDecimal;

public record TourSearchItemDto(
       Long hotelbedsCode,
       String name,
       int rating,
       String location,
       String imageUrl,
       BigDecimal minPrice
) {}
