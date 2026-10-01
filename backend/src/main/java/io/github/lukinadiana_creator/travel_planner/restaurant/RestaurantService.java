package io.github.lukinadiana_creator.travel_planner.restaurant;

import io.github.lukinadiana_creator.travel_planner.exception.ExternalServiceException;
import io.github.lukinadiana_creator.travel_planner.google.RestaurantGoogleMapper;
import io.github.lukinadiana_creator.travel_planner.google.RestaurantGoogleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class RestaurantService {
    private static final Logger log = LoggerFactory.getLogger(RestaurantService.class);
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final WebClient webClientGooglePlaces;
    private final RestaurantGoogleMapper restaurantGoogleMapper;

    public RestaurantService(RestaurantRepository restaurantRepository, RestaurantMapper restaurantMapper, WebClient webClientGooglePlaces, RestaurantGoogleMapper restaurantGoogleMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
        this.webClientGooglePlaces = webClientGooglePlaces;
        this.restaurantGoogleMapper = restaurantGoogleMapper;
    }

    public List<RestaurantDto> getRestaurants(String city) {
        if (restaurantRepository.existsBySearchCityIgnoreCase(city.trim())) {
            return restaurantRepository.findAllBySearchCityIgnoreCase(city.trim()).stream()
                    .map(restaurantMapper::toDto).toList();
        }

        Map<String, Object> body = Map.of("textQuery", "Лучшие рестораны в " + city,
                "languageCode", "ru",
                "maxResultCount", 20);


        RestaurantGoogleResponse response;
        try {
            response = webClientGooglePlaces.post()
                    .uri("/places:searchText")
                    .header("X-Goog-FieldMask", "places.primaryTypeDisplayName,places.displayName,places.rating,places.formattedAddress,places.location,places.priceRange")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(RestaurantGoogleResponse.class)
                    .block();
        } catch (WebClientResponseException | WebClientRequestException e) {
            throw new ExternalServiceException("Не удалось получить данные о ресторанах, попробуйте позже", e);
        }

        if (response == null || response.places() == null || response.places().isEmpty()) {
            return List.of();
        }

        List<RestaurantDto> restaurants = response.places().stream()
                .map(restaurantGoogleMapper::toDto).toList();

        List<Restaurant> entities = restaurants.stream()
                .map(restaurantMapper::toEntity)
                .toList();
        entities.forEach(e -> e.setSearchCity(city.trim()));

        try {
            List<Restaurant> savedEntities = restaurantRepository.saveAll(entities);
            return savedEntities.stream()
                    .map(restaurantMapper::toDto).toList();
        } catch (DataAccessException e) {
            log.warn("Не удалось сохранить достопримечательности в кеш для города", city, e);
            return restaurants;
        }
    }

//    private String searchPhoto(String nameRestaurant) {
//        UnsplashSearchResponse response = webClientUnsplash.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/search/photos")
//                        .queryParam("query", nameRestaurant)
//                        .queryParam("per_page", 1)
//                        .build())
//                .retrieve()
//                .bodyToMono(UnsplashSearchResponse.class)
//                .block();
//
//        if (response == null || response.results().isEmpty()) return null;
//
//        return response
//                .results()
//                .get(0)
//                .urls()
//                .regular();
//    }
}
