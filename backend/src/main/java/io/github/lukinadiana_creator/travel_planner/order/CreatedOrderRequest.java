package io.github.lukinadiana_creator.travel_planner.order;

import io.github.lukinadiana_creator.travel_planner.tour.SaveTourRequest;

public record CreatedOrderRequest(
        SaveTourRequest tourRequest
) {}
