package com.smart.smart_backend.application.dto.habit.log;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StudyLogRequestDto(
    @NotNull(message = "Habit ID is required")
    Long habitId,
    
    @NotNull(message = "Entry ID is required")
    Long entryId,
    
    boolean studied,
    
    @Min(value = 0, message = "Hours cannot be negative")
    @Max(value = 12, message = "Hours cannot exceed 12")
    Float hours,
    
    String subject,
    String skipReason
) {}
