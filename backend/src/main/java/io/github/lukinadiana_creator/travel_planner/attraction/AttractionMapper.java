package io.github.lukinadiana_creator.travel_planner.attraction;

import org.springframework.stereotype.Component;

@Component
public class AttractionMapper {
    public AttractionDto toDto(Attraction attraction) {
        return new AttractionDto(
                attraction.getId(),
                attraction.getAttractionType(),
                attraction.getName(),
                attraction.getLocation(),
                attraction.getLatitude(),
                attraction.getLongitude(),
                attraction.getDescription(),
                attraction.getImageUrl()
        );
    }

    public Attraction toEntity(AttractionDto dto) {
        Attraction attraction = new Attraction();
        attraction.setAttractionType(dto.attractionType());
        attraction.setName(dto.name());
        attraction.setLocation(dto.location());
        attraction.setLatitude(dto.latitude());
        attraction.setLongitude(dto.longitude());
        attraction.setDescription(dto.description());
        attraction.setImageUrl(dto.imageUrl());

        return attraction;
    }
}
