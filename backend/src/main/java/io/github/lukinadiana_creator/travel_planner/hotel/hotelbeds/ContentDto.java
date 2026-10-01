package io.github.lukinadiana_creator.travel_planner.hotel.hotelbeds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ContentDto(
        String content
) {}
