package io.github.lukinadiana_creator.travel_planner.favorite;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/attractions/{id}")
    public void addAttractionsToFavorite(@PathVariable Long id, Authentication authentication) {
        favoriteService.addAttractionsToFavorite(id, authentication.getName());
    }

    @PostMapping("/restaurants/{id}")
    public void addRestaurantToFavorite(@PathVariable Long id, Authentication authentication) {
        favoriteService.addRestaurantToFavorite(id, authentication.getName());
    }

    @PostMapping("/hotels/{id}")
    public void addHotelToFavorite(@PathVariable Long id, Authentication authentication) {
        favoriteService.addHotelToFavorite(id, authentication.getName());
    }

    @GetMapping("/attractions")
    public List<FavoriteAttractionDto> getFavoriteAttractions(Authentication authentication) {
        return favoriteService.getFavoriteAttractions(authentication.getName());
    }

    @GetMapping("/restaurants")
    public List<FavoriteRestaurantDto> getFavoriteRestaurants(Authentication authentication) {
        return favoriteService.getFavoriteRestaurants(authentication.getName());
    }

    @GetMapping("/hotels")
    public List<FavoriteHotelDto> getFavoriteHotels(Authentication authentication) {
        return favoriteService.getFavoriteHotels(authentication.getName());
    }

    @DeleteMapping("/attractions/{id}")
    public void deleteFavoriteAttraction(@PathVariable Long id, Authentication authentication) {
        favoriteService.deleteFavoriteAttraction(id, authentication.getName());
    }

    @DeleteMapping("/restaurants/{id}")
    public void deleteFavoriteRestaurant(@PathVariable Long id, Authentication authentication) {
        favoriteService.deleteFavoriteRestaurant(id, authentication.getName());
    }

    @DeleteMapping("/hotels/{id}")
    public void deleteFavoriteHotel(@PathVariable Long id, Authentication authentication) {
        favoriteService.deleteFavoriteHotel(id, authentication.getName());
    }
}
