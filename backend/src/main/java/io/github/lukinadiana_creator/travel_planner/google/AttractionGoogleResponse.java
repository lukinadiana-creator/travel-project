package io.github.lukinadiana_creator.travel_planner.google;

import java.util.List;

public record AttractionGoogleResponse(
        List<AttractionGoogleDto> places
) { }
