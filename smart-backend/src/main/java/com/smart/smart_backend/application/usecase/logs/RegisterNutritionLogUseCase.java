package com.smart.smart_backend.application.usecase.logs;

import com.smart.smart_backend.application.dto.habit.log.NutritionLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.NutritionLogResponseDto;
import com.smart.smart_backend.application.mapper.logs.NutritionLogMapper;
import com.smart.smart_backend.application.port.in.logs.RegisterNutritionLog;
import com.smart.smart_backend.application.port.out.habit.HabitRepositoryPort;
import com.smart.smart_backend.application.port.out.logs.NutritionLogRepositoryPort;
import com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort;
import com.smart.smart_backend.domain.enums.HabitType;
import com.smart.smart_backend.domain.exception.DuplicateHabitLogException;
import com.smart.smart_backend.domain.exception.EntryNotFoundException;
import com.smart.smart_backend.domain.exception.HabitNotFoundException;
import com.smart.smart_backend.domain.exception.HabitTypeMisMatchException;
import com.smart.smart_backend.domain.model.habit.Habit;
import com.smart.smart_backend.domain.model.habit.NutritionLog;

public class RegisterNutritionLogUseCase implements RegisterNutritionLog {

    private final HabitRepositoryPort habitRepositoryPort;
    private final DailyEntryRepositoryPort dailyEntryRepositoryPort;
    private final NutritionLogRepositoryPort nutritionLogRepositoryPort;

    public RegisterNutritionLogUseCase(HabitRepositoryPort habitRepositoryPort,
            DailyEntryRepositoryPort dailyEntryRepositoryPort, NutritionLogRepositoryPort nutritionLogRepositoryPort) {
        this.habitRepositoryPort = habitRepositoryPort;
        this.dailyEntryRepositoryPort = dailyEntryRepositoryPort;
        this.nutritionLogRepositoryPort = nutritionLogRepositoryPort;
    }

    @Override
    public NutritionLogResponseDto execute(Long userId, NutritionLogRequestDto requestDto) {

        Habit habit = habitRepositoryPort.findByIdAndUserId(requestDto.habitId(), userId)
                .orElseThrow(() -> new HabitNotFoundException(requestDto.habitId()));

        if (habit.getType() != HabitType.NUTRITION) {
            throw new HabitTypeMisMatchException(habit.getType(), HabitType.NUTRITION);
        }

        dailyEntryRepositoryPort.findByIdAndUserId(requestDto.entryId(), userId)
                .orElseThrow(() -> new EntryNotFoundException(requestDto.entryId()));

        if (nutritionLogRepositoryPort.existByHabitIdAndEntryId(requestDto.habitId(), requestDto.entryId())) {
            throw new DuplicateHabitLogException(requestDto.habitId(), requestDto.entryId());
        }

        NutritionLog nutritionLog = NutritionLog.create(
                requestDto.habitId(),
                requestDto.entryId(),
                requestDto.rating(),
                requestDto.hasObservation(),
                requestDto.metGoal());

        NutritionLog savedLog = nutritionLogRepositoryPort.save(nutritionLog);
        return NutritionLogMapper.toResponse(savedLog);
    }
}
