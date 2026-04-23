package com.smart.smart_backend.domain.model.habit;

import com.smart.smart_backend.domain.enums.NutritionRating;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NutritionLog {
    private Long id;
    private Long habitId; //FK
    private Long entryId; // FK
    private NutritionRating rating;
    private boolean hasObservation;
    private boolean metGoal; // nulo si hasObservation is false
}
