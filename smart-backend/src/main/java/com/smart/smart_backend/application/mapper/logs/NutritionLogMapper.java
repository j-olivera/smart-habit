package com.smart.smart_backend.application.mapper.logs;

import com.smart.smart_backend.application.dto.habit.log.NutritionLogResponseDto;
import com.smart.smart_backend.domain.model.habit.NutritionLog;

public class NutritionLogMapper {
    public static NutritionLogResponseDto toResponse(NutritionLog nutritionLog) {
        return new NutritionLogResponseDto(
                nutritionLog.getId(),
                nutritionLog.getHabitId(),
                nutritionLog.getEntryId(),
                nutritionLog.getRating(),
                nutritionLog.isHasObservation(),
                nutritionLog.isMetGoal());
    }
}
