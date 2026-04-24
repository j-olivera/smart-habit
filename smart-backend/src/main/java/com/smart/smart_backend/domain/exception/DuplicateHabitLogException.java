package com.smart.smart_backend.domain.exception;

public class DuplicateHabitLogException extends RuntimeException {
    public DuplicateHabitLogException(Long habitId, Long entryId) {
        super("Already exists log for habit with id=%d in entry=%d".formatted(habitId,entryId));
    }
}
