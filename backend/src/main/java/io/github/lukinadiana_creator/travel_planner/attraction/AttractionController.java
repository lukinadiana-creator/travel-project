package io.github.lukinadiana_creator.travel_planner.attraction;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attractions")
@Validated
public class AttractionController {
    private final AttractionService attractionService;

    public AttractionController(AttractionService attractionService) {
        this.attractionService = attractionService;
    }

    @GetMapping
    public List<AttractionDto> getAttractions(@RequestParam @NotBlank(message = "Город не должен быть пустым") String city) {
        return attractionService.getAttractions(city);
    }

}