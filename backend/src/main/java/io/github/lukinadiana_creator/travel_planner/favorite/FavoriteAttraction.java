package io.github.lukinadiana_creator.travel_planner.favorite;

import io.github.lukinadiana_creator.travel_planner.attraction.Attraction;
import io.github.lukinadiana_creator.travel_planner.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "favorite_attractions")
public class FavoriteAttraction {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "attraction_id")
    private Attraction attraction;

    public FavoriteAttraction() {}

    public FavoriteAttraction(User user, Attraction attraction) {
        this.user = user;
        this.attraction = attraction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
    }
}
