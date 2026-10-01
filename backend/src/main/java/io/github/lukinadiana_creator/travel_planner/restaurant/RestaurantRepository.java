package io.github.lukinadiana_creator.travel_planner.restaurant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    boolean existsBySearchCityIgnoreCase(String city);
    List<Restaurant> findAllBySearchCityIgnoreCase(String city);
}
