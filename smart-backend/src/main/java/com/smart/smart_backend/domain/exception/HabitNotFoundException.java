package com.smart.smart_backend.domain.exception;

public class HabitNotFoundException extends RuntimeException {
    public HabitNotFoundException(Long habitId) {
        super(
                "Habit with id=%d not found.".formatted(habitId)
        );
    }
}
