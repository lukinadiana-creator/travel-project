package io.github.lukinadiana_creator.travel_planner.order;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderDto(
        Long id,
        String hotelName,
        String location,
        LocalDate startTour,
        LocalDate endTour,
        int adults,
        BigDecimal amount,
        OrderStatus orderStatus
) {}
