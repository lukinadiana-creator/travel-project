package io.github.lukinadiana_creator.travel_planner.hotel;

import java.util.List;

public record HotelDto(
        Long hotelbedsCode,
        String name,
        int rating,
        String location,
        String description,
        double latitude,
        double longitude,
        List<String> imageUrls,
        List<RoomDto> rooms
) {}
