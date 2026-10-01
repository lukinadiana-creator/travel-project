package io.github.lukinadiana_creator.travel_planner.exception;

public class ExternalServiceException extends RuntimeException{
    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
