package io.github.lukinadiana_creator.travel_planner.favorite;

import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {
    public FavoriteAttractionDto toDto(FavoriteAttraction entity) {
        return new FavoriteAttractionDto(
                entity.getAttraction().getId(),
                entity.getAttraction().getAttractionType(),
                entity.getAttraction().getName(),
                entity.getAttraction().getLocation(),
                entity.getAttraction().getDescription(),
                entity.getAttraction().getImageUrl()
        );
    }

    public FavoriteRestaurantDto toDto(FavoriteRestaurant entity) {
        return new FavoriteRestaurantDto(
                entity.getRestaurant().getId(),
                entity.getRestaurant().getFoodType(),
                entity.getRestaurant().getName(),
                entity.getRestaurant().getRating(),
                entity.getRestaurant().getLocation(),
                entity.getRestaurant().getAverageBill(),
                entity.getRestaurant().getImageUrl()
        );
    }

    public FavoriteHotelDto toDto(FavoriteHotel entity) {
        return new FavoriteHotelDto(
                entity.getHotel().getHotelbedsCode(),
                entity.getHotel().getName(),
                entity.getHotel().getRating(),
                entity.getHotel().getLocation(),
                entity.getHotel().getImageUrls().get(0)
        );
    }
}
