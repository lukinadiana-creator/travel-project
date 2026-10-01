package io.github.lukinadiana_creator.travel_planner.restaurant;

import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {

    public RestaurantDto toDto(Restaurant restaurant) {
        return new RestaurantDto(
                restaurant.getId(),
                restaurant.getFoodType(),
                restaurant.getName(),
                restaurant.getRating(),
                restaurant.getLocation(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                restaurant.getAverageBill(),
                restaurant.getImageUrl()
        );
    }

    public Restaurant toEntity(RestaurantDto dto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setFoodType(dto.foodType());
        restaurant.setName(dto.name());
        restaurant.setRating(dto.rating());
        restaurant.setLocation(dto.location());
        restaurant.setLatitude(dto.latitude());
        restaurant.setLongitude(dto.longitude());
        restaurant.setAverageBill(dto.averageBill());
        restaurant.setImageUrl(dto.imageUrl());

        return restaurant;
    }
}
