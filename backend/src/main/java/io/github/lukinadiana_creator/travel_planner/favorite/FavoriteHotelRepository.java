package io.github.lukinadiana_creator.travel_planner.favorite;

import io.github.lukinadiana_creator.travel_planner.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteHotelRepository extends JpaRepository<FavoriteHotel, Long> {
    List<FavoriteHotel> findAllByUser(User user);
    void deleteByUserAndHotelId(User user, Long hotelId);
    void deleteByUserAndHotel_HotelbedsCode(User user, Long hotelbedsCode);
}
