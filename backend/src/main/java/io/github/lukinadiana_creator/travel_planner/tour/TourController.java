package io.github.lukinadiana_creator.travel_planner.tour;

import io.github.lukinadiana_creator.travel_planner.flight.FlightPairDto;
import io.github.lukinadiana_creator.travel_planner.hotel.HotelDto;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/tours")
@Validated
public class TourController {
    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping("/{hotelbedsCode}")
    public HotelDto getInfoAboutTour(@PathVariable Long hotelbedsCode,
                                     @RequestParam @FutureOrPresent LocalDate checkIn,
                                     @RequestParam @Min(1)  int nightCount,
                                     @RequestParam @Min(1) int adults) {
        return tourService.getInfoAboutTour(hotelbedsCode, checkIn, nightCount, adults);
    }

    @GetMapping
    public List<TourSearchItemDto> searchTours(@RequestParam @NotBlank(message = "Город не должен быть пустым") String cityFrom,
                                               @RequestParam @NotBlank(message = "Город не должен быть пустым") String cityTo,
                                               @RequestParam @NotNull @FutureOrPresent LocalDate checkIn,
                                               @RequestParam @Min(1)  int nightCount,
                                               @RequestParam @Min(1) int adults) {
        return tourService.searchTours(cityFrom, cityTo, checkIn, nightCount, adults);
    }

    @GetMapping("/{hotelbedsCode}/flights")
    public List<FlightPairDto> getFlightOptions(@RequestParam @NotBlank String cityFrom,
                                                @RequestParam @NotBlank String cityTo,
                                                @RequestParam @NotNull @FutureOrPresent LocalDate departureDate,
                                                @RequestParam @Min(1) int nightCount) {
        return tourService.getFlightOptions(cityFrom, cityTo, departureDate, nightCount);
    }

//    @PostMapping("/favorite/favorite-tours")
//    public void addToursToFavorite(@RequestBody SaveTourRequest request, Authentication authentication) {
//        tourService.addToursToFavorite(request, authentication.getName());
//    }
}
