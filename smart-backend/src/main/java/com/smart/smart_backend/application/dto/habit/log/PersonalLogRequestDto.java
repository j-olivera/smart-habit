package com.smart.smart_backend.application.dto.habit.log;

import jakarta.validation.constraints.NotNull;

public record PersonalLogRequestDto(
    @NotNull(message = "Habit ID is required")
    Long habitId,
    
    @NotNull(message = "Entry ID is required")
    Long entryId,
    
    boolean completed,
    Float hours,
    String description
) {}
