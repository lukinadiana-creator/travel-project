package io.github.lukinadiana_creator.travel_planner.exception;

public record FieldError(
        String field,
        String message
) {
}
