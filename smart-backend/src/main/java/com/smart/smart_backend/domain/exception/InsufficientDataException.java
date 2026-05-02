package com.smart.smart_backend.domain.exception;

public class InsufficientDataException extends RuntimeException {

    public InsufficientDataException(int daysFound, int minRequired) {
        super(
                "At least %d logged days are required. Days found: %d"
                        .formatted(minRequired, daysFound));
    }
}