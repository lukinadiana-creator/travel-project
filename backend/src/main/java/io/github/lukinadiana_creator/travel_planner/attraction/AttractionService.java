package io.github.lukinadiana_creator.travel_planner.attraction;

import io.github.lukinadiana_creator.travel_planner.exception.ExternalServiceException;
import io.github.lukinadiana_creator.travel_planner.google.AttractionGoogleMapper;
import io.github.lukinadiana_creator.travel_planner.google.AttractionGoogleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class AttractionService {
    private static final Logger log = LoggerFactory.getLogger(AttractionService.class);
    private final AttractionRepository attractionRepository;
    private final WebClient webClientGooglePlaces;
    private final AttractionGoogleMapper attractionGoogleMapper;
    private final AttractionMapper attractionMapper;

    public AttractionService(AttractionRepository attractionRepository, WebClient webClientGooglePlaces, AttractionGoogleMapper attractionGoogleMapper, AttractionMapper attractionMapper) {
        this.attractionRepository = attractionRepository;
        this.webClientGooglePlaces = webClientGooglePlaces;
        this.attractionGoogleMapper = attractionGoogleMapper;
        this.attractionMapper = attractionMapper;
    }

    public List<AttractionDto> getAttractions(String city) {
        if (city!= null && attractionRepository.existsBySearchCityIgnoreCase(city.trim())) {
            return attractionRepository.findALLBySearchCityIgnoreCase(city.trim())
                    .stream()
                    .map(attractionMapper::toDto)
                    .toList();
        }

        Map<String, Object> body = Map.of(
                "textQuery", "Популярные достопримечательности в " + city,
                "languageCode", "ru",
                "maxResultCount", 20
        );

        AttractionGoogleResponse response;

        try{
            response = webClientGooglePlaces.post()
                    .uri("/places:searchText")
                    .header("X-Goog-FieldMask", "places.primaryTypeDisplayName,places.displayName,places.formattedAddress,places.location,places.editorialSummary")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(AttractionGoogleResponse.class)
                    .block();
        } catch (WebClientResponseException | WebClientRequestException e) {
            throw new ExternalServiceException("Не удалось получить данные о достопримечательностях, попробуйте позже", e);
        }

        if (response == null || response.places() == null || response.places().isEmpty()) {
            return List.of();
        }

        List<AttractionDto> attractions = response.places()
                .stream()
                .map(attractionGoogleMapper::toDto)
                .toList();

        List<Attraction> entities = attractions.stream()
                .map(attractionMapper::toEntity).toList();
        entities.forEach(e -> e.setSearchCity(city.trim()));

        try {
            List<Attraction> savedEntities = attractionRepository.saveAll(entities);
            return savedEntities.stream()
                    .map(attractionMapper::toDto).toList();
        } catch (DataAccessException e) {
            log.warn("Не удалось сохранить достопримечательности в кеш для города", city, e);
            return attractions;
        }
    }

//    private String searchPhoto(String nameAttraction) {
//        UnsplashSearchResponse response = webClientUnsplash.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/search/photos")
//                        .queryParam("query", nameAttraction)
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

//    private String searchPhoto(String nameAttraction) {
//        WikimediaResponse response = webClientWikimedia.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/w/api.php")
//                        .queryParam("action", "query")
//                        .queryParam("generator", "search")
//                        .queryParam("gsrsearch", nameAttraction)
//                        .queryParam("gsrnamespace", 6)
//                        .queryParam("prop", "imageinfo")
//                        .queryParam("iiprop", "url")
//                        .queryParam("iiurlwidth", 800)
//                        .queryParam("format", "json")
//                        .build())
//                .retrieve()
//                .bodyToMono(WikimediaResponse.class)
//                .block();
//
//        if (response == null || response.query() == null) {
//            return null;
//        }
//
//        return response.query()
//                .pages().values()
//                .stream()
//                .filter(page -> page.imageinfo() != null)
//                .flatMap(page -> page.imageinfo().stream())
//                .map(WikimediaImageInfo::url)
//                .findFirst()
//                .orElse(null);
//    }
}