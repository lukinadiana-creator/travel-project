package io.github.lukinadiana_creator.travel_planner.hotel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByDestinationCode(String destinationCode);
    Optional<Hotel> findByHotelbedsCode(Long hotelbedsCode);
}
