package io.github.lukinadiana_creator.travel_planner.hotel.hotelbeds;

import java.util.List;

public record AvailabilityRequest(
    StayDto stay,
    List<OccupancyDto> occupancies,
    HotelsRequestDto hotels
) {}
