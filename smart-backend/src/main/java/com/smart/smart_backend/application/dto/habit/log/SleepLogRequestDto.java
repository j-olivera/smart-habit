package com.smart.smart_backend.application.dto.habit.log;

import com.smart.smart_backend.domain.enums.SleepQuality;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SleepLogRequestDto(
    @NotNull(message = "Habit ID is required")
    Long habitId,
    
    @NotNull(message = "Entry ID is required")
    Long entryId,
    
    @NotNull(message = "Hours are required")
    @Min(value = 0, message = "Hours cannot be negative")
    @Max(value = 12, message = "Hours cannot exceed 12")
    Float hours,
    
    @NotNull(message = "Quality is required")
    SleepQuality quality,
    
    boolean napped,
    
    @Min(value = 0, message = "Nap hours cannot be negative")
    @Max(value = 4, message = "Nap hours cannot exceed 4")
    Float napHours,
    
    boolean napNeeded
) {}
