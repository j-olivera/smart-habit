package com.smart.smart_backend.application.port.out.logs;

import com.smart.smart_backend.domain.model.habit.NutritionLog;

public interface NutritionLogRepositoryPort {
    NutritionLog save(NutritionLog nutritionLog);
    boolean existByHabitIdAndEntryId(Long habitId, Long entryId);
}
