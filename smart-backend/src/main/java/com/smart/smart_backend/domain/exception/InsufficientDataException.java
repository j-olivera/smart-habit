package com.smart.smart_backend.domain.exception;

public class InsufficientDataException extends RuntimeException {

    public InsufficientDataException(int daysFound, int minRequired) {
        super(
            "Se necesitan al menos %d días registrados. Días encontrados: %d"
                .formatted(minRequired, daysFound)
        );
    }
}