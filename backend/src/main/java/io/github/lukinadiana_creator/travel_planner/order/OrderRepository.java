package io.github.lukinadiana_creator.travel_planner.order;

import io.github.lukinadiana_creator.travel_planner.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser(User user);
}
