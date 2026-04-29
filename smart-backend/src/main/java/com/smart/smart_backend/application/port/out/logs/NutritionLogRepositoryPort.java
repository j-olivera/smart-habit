package com.smart.smart_backend.application.port.out.logs;

import com.smart.smart_backend.domain.model.habit.NutritionLog;

import java.util.Optional;

public interface NutritionLogRepositoryPort {
    NutritionLog save(NutritionLog nutritionLog);
    boolean existByEntryId(Long entryId);
    Optional<NutritionLog> findByEntryId(Long entryId);
}
