package io.github.lukinadiana_creator.travel_planner.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        List<FieldError> fieldErrors,
        String path
) {
    public static ValidationErrorResponse of(List<FieldError> fieldErrors, String path) {
        return new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                fieldErrors,
                path
        );
    }
}
