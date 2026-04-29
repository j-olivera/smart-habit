package com.smart.smart_backend.application.dto.habit.log;

import com.smart.smart_backend.domain.enums.NutritionRating;
import jakarta.validation.constraints.NotNull;

public record NutritionLogRequestDto(

    @NotNull(message = "Entry ID is required")
    Long entryId,
    
    @NotNull(message = "Rating is required")
    NutritionRating rating,
    
    boolean hasObservation,
    boolean metGoal
) {}
