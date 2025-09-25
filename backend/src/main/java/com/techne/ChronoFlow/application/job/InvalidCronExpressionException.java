package com.techne.ChronoFlow.application.job;

public class InvalidCronExpressionException extends RuntimeException {
    public InvalidCronExpressionException(String message) {
        super(message);
    }

    public InvalidCronExpressionException(String message, Throwable cause) {
        super(message, cause);
    }
}
