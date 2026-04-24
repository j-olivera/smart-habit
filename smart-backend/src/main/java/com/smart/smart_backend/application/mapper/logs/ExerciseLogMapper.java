package com.smart.smart_backend.application.mapper.logs;

import com.smart.smart_backend.application.dto.habit.log.ExerciseLogResponseDto;
import com.smart.smart_backend.domain.model.habit.ExerciseLog;

public class ExerciseLogMapper {
    public static ExerciseLogResponseDto toResponse(ExerciseLog exerciseLog) {
        return new ExerciseLogResponseDto(
                exerciseLog.getId(),
                exerciseLog.getHabitId(),
                exerciseLog.getEntryId(),
                exerciseLog.isExercised(),
                exerciseLog.getHours(),
                exerciseLog.getMuscularGroup(),
                exerciseLog.getEnergyLevel(),
                exerciseLog.getSkipReason());
    }
}
