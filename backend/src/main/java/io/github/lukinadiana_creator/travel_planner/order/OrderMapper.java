package io.github.lukinadiana_creator.travel_planner.order;

import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderDto toDto(Order order) {
        return new OrderDto(
                order.getId(),
                order.getTour().getHotel().getName(),
                order.getTour().getHotel().getLocation(),
                order.getTour().getRoom().getArrivalDate(),
                order.getTour().getRoom().getDepartureDate(),
                order.getTour().getRoom().getNumberOfPerson(),
                order.getTour().getTotalPrice(),
                order.getOrderStatus()
        );
    }
}
