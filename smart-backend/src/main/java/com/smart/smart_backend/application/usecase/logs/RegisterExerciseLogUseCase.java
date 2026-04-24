package com.smart.smart_backend.application.usecase.logs;

import com.smart.smart_backend.application.dto.habit.log.ExerciseLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.ExerciseLogResponseDto;
import com.smart.smart_backend.application.mapper.logs.ExerciseLogMapper;
import com.smart.smart_backend.application.port.in.logs.RegisterExerciseLog;
import com.smart.smart_backend.application.port.out.habit.HabitRepositoryPort;
import com.smart.smart_backend.application.port.out.logs.ExerciseLogRepositoryPort;
import com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort;
import com.smart.smart_backend.domain.enums.HabitType;
import com.smart.smart_backend.domain.exception.DuplicateHabitLogException;
import com.smart.smart_backend.domain.exception.EntryNotFoundException;
import com.smart.smart_backend.domain.exception.HabitNotFoundException;
import com.smart.smart_backend.domain.exception.HabitTypeMisMatchException;
import com.smart.smart_backend.domain.model.habit.ExerciseLog;
import com.smart.smart_backend.domain.model.habit.Habit;

public class RegisterExerciseLogUseCase implements RegisterExerciseLog {

    private final HabitRepositoryPort habitRepositoryPort;
    private final DailyEntryRepositoryPort dailyEntryRepositoryPort;
    private final ExerciseLogRepositoryPort exerciseLogRepositoryPort;

    public RegisterExerciseLogUseCase(HabitRepositoryPort habitRepositoryPort,
            DailyEntryRepositoryPort dailyEntryRepositoryPort, ExerciseLogRepositoryPort exerciseLogRepositoryPort) {
        this.habitRepositoryPort = habitRepositoryPort;
        this.dailyEntryRepositoryPort = dailyEntryRepositoryPort;
        this.exerciseLogRepositoryPort = exerciseLogRepositoryPort;
    }

    @Override
    public ExerciseLogResponseDto execute(Long userId, ExerciseLogRequestDto requestDto) {

        Habit habit = habitRepositoryPort.findByIdAndUserId(requestDto.habitId(), userId)
                .orElseThrow(() -> new HabitNotFoundException(requestDto.habitId()));

        if (habit.getType() != HabitType.EXERCISE) {
            throw new HabitTypeMisMatchException(habit.getType(), HabitType.EXERCISE);
        }

        dailyEntryRepositoryPort.findByIdAndUserId(requestDto.entryId(), userId)
                .orElseThrow(() -> new EntryNotFoundException(requestDto.entryId()));

        ExerciseLog exerciseLog = exerciseLogRepositoryPort.findByHabitIdAndEntryId(requestDto.habitId(), requestDto.entryId())
                .map(existing -> existing.update(
                        requestDto.exercised(),
                        requestDto.hours(),
                        requestDto.muscularGroup(),
                        requestDto.energyLevel(),
                        requestDto.skipReason()
                ))
                .orElseGet(() -> ExerciseLog.create(
                        requestDto.habitId(),
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
