package io.github.lukinadiana_creator.travel_planner.hotel;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {
    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public List<HotelDto> searchHotels(@RequestParam @NotBlank String city,
                                       @RequestParam @NotNull @FutureOrPresent LocalDate checkIn,
                                       @RequestParam @NotNull @FutureOrPresent LocalDate checkOut,
                                       @RequestParam @Min(1) int adults) {
        return hotelService.searchHotels(city, checkIn, checkOut, adults);
    }
}
