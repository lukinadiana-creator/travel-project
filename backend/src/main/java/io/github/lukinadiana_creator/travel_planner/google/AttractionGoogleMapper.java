package io.github.lukinadiana_creator.travel_planner.google;

import io.github.lukinadiana_creator.travel_planner.attraction.AttractionDto;
import org.springframework.stereotype.Component;

@Component
public class AttractionGoogleMapper {
    private final PhotoService photoService;

    public AttractionGoogleMapper(PhotoService photoService) {
        this.photoService = photoService;
    }

    public AttractionDto toDto (AttractionGoogleDto googleDto) {
        return new AttractionDto(
                null,
                googleDto.primaryTypeDisplayName().text(),
                googleDto.displayName().text(),
                googleDto.formattedAddress(),
                googleDto.location().latitude(),
                googleDto.location().longitude(),
                googleDto.editorialSummary() != null ? googleDto.editorialSummary().text() : null,
                null
        );
    }
}
