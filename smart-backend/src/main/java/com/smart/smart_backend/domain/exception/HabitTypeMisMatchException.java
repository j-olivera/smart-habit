package com.smart.smart_backend.domain.exception;

import com.smart.smart_backend.domain.enums.HabitType;

public class HabitTypeMisMatchException extends RuntimeException {
    public HabitTypeMisMatchException(HabitType actual, HabitType expected) {
        super("The habit types don't match. Expected:%s | Actual:%s".formatted(actual,expected));
    }
}
