package io.github.lukinadiana_creator.travel_planner.flight;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/flights")
@Validated
public class FlightController {
    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping
    public List<FlightPairDto> getFlight(@RequestParam @NotBlank String cityFrom,
                                         @RequestParam @NotBlank String cityTo,
                                         @RequestParam @FutureOrPresent LocalDate departureDate,
                                         @RequestParam @Positive int nightCount) {
        return flightService.getFlight(cityFrom, cityTo, departureDate, nightCount);
    }
}
