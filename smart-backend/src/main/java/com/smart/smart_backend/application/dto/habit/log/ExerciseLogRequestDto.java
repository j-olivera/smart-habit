package com.smart.smart_backend.application.dto.habit.log;

import com.smart.smart_backend.domain.enums.MuscularGroup;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ExerciseLogRequestDto(

    @NotNull(message = "Entry ID is required")
    Long entryId,
    
    boolean exercised,
    
    @Min(value = 0, message = "Hours cannot be negative")
    @Max(value = 4, message = "Hours cannot exceed 4")
    Float hours,
    
    MuscularGroup muscularGroup,
    
    @Min(value = 1, message = "Energy level must be at least 1")
    @Max(value = 100, message = "Energy level cannot exceed 100")
    Integer energyLevel,
    
    String skipReason
) {}
