package com.smart.smart_backend.application.dto.habit.log;

import com.smart.smart_backend.domain.enums.NutritionRating;
import lombok.Builder;

@Builder
public record NutritionLogResponseDto(
    Long id,

    Long entryId,
    NutritionRating rating,
    boolean hasObservation,
    boolean metGoal
) {}
