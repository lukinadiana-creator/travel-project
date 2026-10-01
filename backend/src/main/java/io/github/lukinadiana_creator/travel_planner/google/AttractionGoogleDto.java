package io.github.lukinadiana_creator.travel_planner.google;

import java.util.List;

public record AttractionGoogleDto(
//        String primaryType,
        DisplayName primaryTypeDisplayName,
        DisplayName displayName,
        String formattedAddress,
        Location location,
        EditorialSummary editorialSummary,
        List<Photo> photos
) { }
