package io.github.lukinadiana_creator.travel_planner.flight;

import java.math.BigDecimal;

public record FlightPairDto(
        FlightDto outboundFlight,
        FlightDto returnFlight,
        BigDecimal totalPrice
) {}
