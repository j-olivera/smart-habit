package com.smart.smart_backend.application.usecase.logs;

import com.smart.smart_backend.application.dto.habit.log.NutritionLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.NutritionLogResponseDto;
import com.smart.smart_backend.application.mapper.logs.NutritionLogMapper;
import com.smart.smart_backend.application.port.in.logs.RegisterNutritionLog;
import com.smart.smart_backend.application.port.out.habit.HabitRepositoryPort;
import com.smart.smart_backend.application.port.out.logs.NutritionLogRepositoryPort;
import com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort;
import com.smart.smart_backend.domain.exception.DuplicateHabitLogException;
import com.smart.smart_backend.domain.exception.EntryNotFoundException;
import com.smart.smart_backend.domain.model.habit.NutritionLog;

public class RegisterNutritionLogUseCase implements RegisterNutritionLog {

    private final DailyEntryRepositoryPort dailyEntryRepositoryPort;
    private final NutritionLogRepositoryPort nutritionLogRepositoryPort;

    public RegisterNutritionLogUseCase(DailyEntryRepositoryPort dailyEntryRepositoryPort, NutritionLogRepositoryPort nutritionLogRepositoryPort) {
        this.dailyEntryRepositoryPort = dailyEntryRepositoryPort;
        this.nutritionLogRepositoryPort = nutritionLogRepositoryPort;
    }

    @Override
    public NutritionLogResponseDto execute(Long userId, NutritionLogRequestDto requestDto) {

        dailyEntryRepositoryPort.findByIdAndUserId(requestDto.entryId(), userId)
                .orElseThrow(() -> new EntryNotFoundException(requestDto.entryId()));

        NutritionLog nutritionLog = nutritionLogRepositoryPort.findByEntryId(requestDto.entryId())
                .map(existing -> existing.update(
                        requestDto.rating(),
                        requestDto.hasObservation(),
                        requestDto.metGoal()
                ))
                .orElseGet(() -> NutritionLog.create(
                        requestDto.entryId(),
                        requestDto.rating(),
                        requestDto.hasObservation(),
                        requestDto.metGoal()));

        NutritionLog savedLog = nutritionLogRepositoryPort.save(nutritionLog);
        return NutritionLogMapper.toResponse(savedLog);
    }
}
