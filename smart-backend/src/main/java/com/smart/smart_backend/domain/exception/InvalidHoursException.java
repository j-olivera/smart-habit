package com.smart.smart_backend.domain.exception;

public class InvalidHoursException extends RuntimeException {
    public InvalidHoursException(String message) {
        super(message);
    }
}
