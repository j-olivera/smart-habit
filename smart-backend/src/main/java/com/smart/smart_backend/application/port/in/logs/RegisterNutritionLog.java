package com.smart.smart_backend.application.port.in.logs;

import com.smart.smart_backend.application.dto.habit.log.NutritionLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.NutritionLogResponseDto;

public interface RegisterNutritionLog {
    NutritionLogResponseDto execute(Long userId, NutritionLogRequestDto requestDto);
}
