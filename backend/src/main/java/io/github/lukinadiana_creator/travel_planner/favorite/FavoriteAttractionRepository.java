package io.github.lukinadiana_creator.travel_planner.favorite;

import io.github.lukinadiana_creator.travel_planner.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteAttractionRepository extends JpaRepository<FavoriteAttraction, Long> {
    List<FavoriteAttraction> findAllByUser(User user);
    void deleteByUserAndAttractionId(User user, Long attractionId);
}
