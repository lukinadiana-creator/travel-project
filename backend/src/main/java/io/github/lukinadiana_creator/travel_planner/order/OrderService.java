package io.github.lukinadiana_creator.travel_planner.order;

import io.github.lukinadiana_creator.travel_planner.exception.EntityNotFoundException;
import io.github.lukinadiana_creator.travel_planner.exception.ExternalServiceException;
import io.github.lukinadiana_creator.travel_planner.tour.Tour;
import io.github.lukinadiana_creator.travel_planner.tour.TourService;
import io.github.lukinadiana_creator.travel_planner.user.User;
import io.github.lukinadiana_creator.travel_planner.user.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TourService tourService;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, TourService tourService, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.tourService = tourService;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderDto createOrder(CreatedOrderRequest request, String firsName, String lastName, String phone, String wishes, String userEmail) {
        User user = userRepository.findUserByEmailIgnoreCase(userEmail).orElseThrow(() -> new EntityNotFoundException("Пользователь с email:" + userEmail + "не найден"));

        Tour tour = tourService.addTour(request.tourRequest());

        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.CREATED);
        order.setFirstName(firsName);
        order.setLastName(lastName);
        order.setPhone(phone);
        order.setWishes(wishes);
        order.setUser(user);
        order.setTour(tour);

        try {
            Order saved = orderRepository.save(order);
            return orderMapper.toDto(saved);
        } catch (DataAccessException e) {
            throw new ExternalServiceException("Не удалось сохранить заказ", e);
        }
    }

    public List<OrderDto> getOrders(String userEmail) {
        User user = userRepository.findUserByEmailIgnoreCase(userEmail).orElseThrow(() -> new EntityNotFoundException("Пользователь с email: " + userEmail + "не найден"));

        List<Order> orders = orderRepository.findAllByUser(user);

        return orders.stream()
                .map(orderMapper::toDto)
                .toList();
    }
}
