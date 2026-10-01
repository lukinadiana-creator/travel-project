package io.github.lukinadiana_creator.travel_planner.favorite;

import io.github.lukinadiana_creator.travel_planner.restaurant.Restaurant;
import io.github.lukinadiana_creator.travel_planner.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "favorite_restaurants")
public class FavoriteRestaurant {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public FavoriteRestaurant() {}

    public FavoriteRestaurant(User user, Restaurant restaurant) {
        this.user = user;
        this.restaurant = restaurant;
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

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
