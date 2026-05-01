package com.smart.smart_backend.application.usecase.logs;

import com.smart.smart_backend.application.dto.habit.log.ExerciseLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.ExerciseLogResponseDto;
import com.smart.smart_backend.application.mapper.logs.ExerciseLogMapper;
import com.smart.smart_backend.application.port.in.logs.RegisterExerciseLog;
import com.smart.smart_backend.application.port.out.logs.ExerciseLogRepositoryPort;
import com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort;
import com.smart.smart_backend.domain.exception.EntryNotFoundException;
import com.smart.smart_backend.domain.model.habit.ExerciseLog;

public class RegisterExerciseLogUseCase implements RegisterExerciseLog {

    private final DailyEntryRepositoryPort dailyEntryRepositoryPort;
    private final ExerciseLogRepositoryPort exerciseLogRepositoryPort;

    public RegisterExerciseLogUseCase(DailyEntryRepositoryPort dailyEntryRepositoryPort,
            ExerciseLogRepositoryPort exerciseLogRepositoryPort) {
        this.dailyEntryRepositoryPort = dailyEntryRepositoryPort;
        this.exerciseLogRepositoryPort = exerciseLogRepositoryPort;
    }

    @Override
    public ExerciseLogResponseDto execute(Long userId, ExerciseLogRequestDto requestDto) {

        dailyEntryRepositoryPort.findByIdAndUserId(requestDto.entryId(), userId)
                .orElseThrow(() -> new EntryNotFoundException(requestDto.entryId()));

        ExerciseLog exerciseLog = exerciseLogRepositoryPort.findByEntryId(requestDto.entryId())
                .map(existing -> existing.update(
                        requestDto.exercised(),
                        requestDto.hours(),
                        requestDto.muscularGroup(),
                        requestDto.energyLevel(),
                        requestDto.skipReason()))
                .orElseGet(() -> ExerciseLog.create(
                        requestDto.entryId(),
                        requestDto.exercised(),
                        requestDto.hours(),
                        requestDto.muscularGroup(),
                        requestDto.energyLevel(),
                        requestDto.skipReason()));

        ExerciseLog savedLog = exerciseLogRepositoryPort.save(exerciseLog);
        return ExerciseLogMapper.toResponse(savedLog);
    }
}
