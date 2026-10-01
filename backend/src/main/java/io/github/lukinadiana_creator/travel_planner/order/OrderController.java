package io.github.lukinadiana_creator.travel_planner.order;

import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderDto createOrder(@RequestBody CreatedOrderRequest request,
                                @RequestParam @NotNull String firstName,
                                @RequestParam @NotNull String lastName,
                                @RequestParam String phone,
                                @RequestParam String wishes,
                                Authentication authentication) {
        return orderService.createOrder(request, firstName, lastName, phone, wishes, authentication.getName());
    }

    @GetMapping
    public List<OrderDto> getOrders(Authentication authentication) {
        return orderService.getOrders(authentication.getName());
    }
}
