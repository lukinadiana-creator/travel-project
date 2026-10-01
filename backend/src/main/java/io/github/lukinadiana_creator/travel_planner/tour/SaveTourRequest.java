package io.github.lukinadiana_creator.travel_planner.tour;

import io.github.lukinadiana_creator.travel_planner.flight.FlightPairDto;
import io.github.lukinadiana_creator.travel_planner.hotel.RoomDto;

public record SaveTourRequest(
       Long hotelbedsCode,
       RoomDto selectedRoom,
       FlightPairDto selectedFlight
) {}
