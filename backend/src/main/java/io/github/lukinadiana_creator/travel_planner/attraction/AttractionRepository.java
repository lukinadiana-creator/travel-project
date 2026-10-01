package io.github.lukinadiana_creator.travel_planner.attraction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Long> {
    boolean existsBySearchCityIgnoreCase(String city);
    List<Attraction> findALLBySearchCityIgnoreCase(String city);
}
