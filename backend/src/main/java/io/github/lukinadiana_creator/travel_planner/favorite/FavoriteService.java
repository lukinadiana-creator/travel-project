package io.github.lukinadiana_creator.travel_planner.favorite;

import io.github.lukinadiana_creator.travel_planner.attraction.Attraction;
import io.github.lukinadiana_creator.travel_planner.attraction.AttractionRepository;
import io.github.lukinadiana_creator.travel_planner.exception.EntityNotFoundException;
import io.github.lukinadiana_creator.travel_planner.exception.ExternalServiceException;
import io.github.lukinadiana_creator.travel_planner.hotel.Hotel;
import io.github.lukinadiana_creator.travel_planner.hotel.HotelRepository;
import io.github.lukinadiana_creator.travel_planner.restaurant.Restaurant;
import io.github.lukinadiana_creator.travel_planner.restaurant.RestaurantRepository;
import io.github.lukinadiana_creator.travel_planner.user.User;
import io.github.lukinadiana_creator.travel_planner.user.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FavoriteService {
    private final FavoriteAttractionRepository favoriteAttractionRepository;
    private final FavoriteHotelRepository favoriteHotelRepository;
    private final FavoriteRestaurantRepository favoriteRestaurantRepository;
    private final AttractionRepository attractionRepository;
    private final RestaurantRepository restaurantRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final FavoriteMapper favoriteMapper;

    public FavoriteService(FavoriteAttractionRepository favoriteAttractionRepository, FavoriteHotelRepository favoriteHotelRepository, FavoriteRestaurantRepository favoriteRestaurantRepository, AttractionRepository attractionRepository, RestaurantRepository restaurantRepository, HotelRepository hotelRepository, UserRepository userRepository, FavoriteMapper favoriteMapper) {
        this.favoriteAttractionRepository = favoriteAttractionRepository;
        this.favoriteHotelRepository = favoriteHotelRepository;
        this.favoriteRestaurantRepository = favoriteRestaurantRepository;
        this.attractionRepository = attractionRepository;
        this.restaurantRepository = restaurantRepository;
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
        this.favoriteMapper = favoriteMapper;
    }

    public void addAttractionsToFavorite(Long id, String userEmail) {
        User user = userRepository.findUserByEmailIgnoreCase(userEmail).orElseThrow(() -> new EntityNotFoundException("Пользователь с email:" + userEmail + "не найден"));
        Attraction attraction = attractionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Достопримечательность не найдена"));

        FavoriteAttraction favoriteAttraction = new FavoriteAttraction();
        favoriteAttraction.setUser(user);
        favoriteAttraction.setAttraction(attraction);

        try {
            favoriteAttractionRepository.save(favoriteAttraction);
        } catch (DataAccessException e) {
            throw new ExternalServiceException("Не удалось сохранить достопримечательность в избранное", e);
        }
    }

    public void addRestaurantToFavorite(Long id, String userEmail) {
        User user = userRepository.findUserByEmailIgnoreCase(userEmail).orElseThrow(() -> new EntityNotFoundException("Пользователь с email:" + userEmail + "не найден"));
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ресторан не найден"));

        FavoriteRestaurant favoriteRestaurant = new FavoriteRestaurant();
        favoriteRestaurant.setUser(user);
        favoriteRestaurant.setRestaurant(restaurant);

        try {
            favoriteRestaurantRepository.save(favoriteRestaurant);
        } catch (DataAccessException e) {
            throw new ExternalServiceException("Не удалось сохранить ресторан в избранное", e);
        }
    }

    public void addHotelToFavorite(Long hotelbedsCode, String userEmail) {
        User user = userRepository.findUserByEmailIgnoreCase(userEmail).orElseThrow(() -> new EntityNotFoundException("Пользователь с email:" + userEmail + "не найден"));
        Hotel hotel = hotelRepository.findByHotelbedsCode(hotelbedsCode).orElseThrow(() -> new EntityNotFoundException("Отель не найден"));

        FavoriteHotel favoriteHotel = new FavoriteHotel();
        favoriteHotel.setUser(user);
        favoriteHotel.setHotel(hotel);

        try {
            favoriteHotelRepository.save(favoriteHotel);
        } catch (DataAccessException e) {
            throw new ExternalServiceException("Не удалось сохранить отель в избранное", e);
        }
    }

    public List<FavoriteAttractionDto> getFavoriteAttractions(String userEmail) {
        User user = userRepository.findUserByEmailIgnoreCase(userEmail).orElseThrow(() -> new EntityNotFoundException("Пользователь с email:" + userEmail + "не найден"));
        List<FavoriteAttraction> favoriteAttractions = favoriteAttractionRepository.findAllByUser(user);

        return favoriteAttractions.stream()
                .map(favoriteMapper::toDto)
                .toList();
    }

    public List<FavoriteRestaurantDto> getFavoriteRestaurants(String userEmail) {
        User user = userRepository.findUserByEmailIgnoreCase(userEmail).orElseThrow(() -> new EntityNotFoundException("Пользователь с email:" + userEmail + "не найден"));
        List<FavoriteRestaurant> favoriteRestaurants = favoriteRestaurantRepository.findAllByUser(user);

        return favoriteRestaurants.stream()
                .map(favoriteMapper::toDto)
                .toList();
    }

    public List<FavoriteHotelDto> getFavoriteHotels(String userEmail) {
        User user = userRepository.findUserByEmailIgnoreCase(userEmail).orElseThrow(() -> new EntityNotFoundException("Пользователь с email:" + userEmail + "не найден"));
        List<FavoriteHotel> favoriteHotels = favoriteHotelRepository.findAllByUser(user);

        return favoriteHotels.stream()
                .map(favoriteMapper::toDto)
                .toList();
    }

    public void deleteFavoriteAttraction(Long id, String userEmail) {
        User user = userRepository.findUserByEmailIgnoreCase(userEmail).orElseThrow(() -> new EntityNotFoundException("Пользователь с email:" + userEmail + "не найден"));
        try {
            favoriteAttractionRepository.deleteByUserAndAttractionId(user, id);
        } catch (DataAccessException e) {
            throw new ExternalServiceException("Не удалось удалить достопримечательность из избранного", e);
        }
    }

    public void deleteFavoriteRestaurant(Long id, String userEmail) {
        User user = userRepository.findUserByEmailIgnoreCase(userEmail).orElseThrow(() -> new EntityNotFoundException("Пользователь с email:" + userEmail + "не найден"));
        try {
            favoriteRestaurantRepository.deleteByUserAndRestaurantId(user, id);
        } catch (DataAccessException e) {
            throw new ExternalServiceException("Не удалось удалить ресторан из избранного", e);
        }
    }

    public void deleteFavoriteHotel(Long hotelbedsCode, String userEmail) {
        User user = userRepository.findUserByEmailIgnoreCase(userEmail).orElseThrow(() -> new EntityNotFoundException("Пользователь с email:" + userEmail + "не найден"));
        try {
            favoriteHotelRepository.deleteByUserAndHotel_HotelbedsCode(user, hotelbedsCode);
        } catch (DataAccessException e) {
            throw new ExternalServiceException("Не удалось удалить отель из избранного", e);
        }
    }
}
