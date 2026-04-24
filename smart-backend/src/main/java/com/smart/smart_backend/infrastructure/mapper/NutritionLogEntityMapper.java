package com.smart.smart_backend.infrastructure.mapper;

import com.smart.smart_backend.domain.enums.NutritionRating;
import com.smart.smart_backend.domain.model.habit.NutritionLog;
import com.smart.smart_backend.infrastructure.model.NutritionLogEntity;
import org.springframework.stereotype.Component;

@Component
public class NutritionLogEntityMapper {

    public NutritionLog toDomain(NutritionLogEntity entity) {
        if (entity == null) return null;
        return new NutritionLog(
                entity.getId(),
                entity.getHabitId(),
                entity.getEntryId(),
                NutritionRating.valueOf(entity.getRating()),
                entity.getHasObservations(),
                entity.getMetGoal()
        );
    }

    public NutritionLogEntity toEntity(NutritionLog domain) {
        if (domain == null) return null;
        return NutritionLogEntity.builder()
                .id(domain.getId())
                .habitId(domain.getHabitId())
                .entryId(domain.getEntryId())
                .rating(domain.getRating().name())
                .hasObservations(domain.isHasObservation())
                .metGoal(domain.isMetGoal())
                .build();
    }
}
