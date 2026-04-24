package com.smart.smart_backend.application.usecase.logs;

import com.smart.smart_backend.application.dto.habit.log.SleepLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.SleepLogResponseDto;
import com.smart.smart_backend.application.mapper.logs.SleepLogMapper;
import com.smart.smart_backend.application.port.in.logs.RegisterSleepLog;
import com.smart.smart_backend.application.port.out.habit.HabitRepositoryPort;
import com.smart.smart_backend.application.port.out.logs.SleepLogRepositoryPort;
import com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort;
import com.smart.smart_backend.domain.enums.HabitType;
import com.smart.smart_backend.domain.exception.DuplicateHabitLogException;
import com.smart.smart_backend.domain.exception.EntryNotFoundException;
import com.smart.smart_backend.domain.exception.HabitNotFoundException;
import com.smart.smart_backend.domain.exception.HabitTypeMisMatchException;
import com.smart.smart_backend.domain.model.habit.Habit;
import com.smart.smart_backend.domain.model.habit.SleepLog;

public class RegisterSleepLogUseCase implements RegisterSleepLog {

    private final HabitRepositoryPort habitRepositoryPort;
    private final DailyEntryRepositoryPort dailyEntryRepositoryPort;
    private final SleepLogRepositoryPort sleepLogRepositoryPort;

    public RegisterSleepLogUseCase(HabitRepositoryPort habitRepositoryPort,
            DailyEntryRepositoryPort dailyEntryRepositoryPort, SleepLogRepositoryPort sleepLogRepositoryPort) {
        this.habitRepositoryPort = habitRepositoryPort;
        this.dailyEntryRepositoryPort = dailyEntryRepositoryPort;
        this.sleepLogRepositoryPort = sleepLogRepositoryPort;
    }

    @Override
    public SleepLogResponseDto execute(Long userId, SleepLogRequestDto requestDto) {

        Habit habit = habitRepositoryPort.findByIdAndUserId(requestDto.habitId(), userId)
                .orElseThrow(() -> new HabitNotFoundException(requestDto.habitId()));

        if (habit.getType() != HabitType.SLEEP) {
            throw new HabitTypeMisMatchException(habit.getType(), HabitType.SLEEP);
        }

        dailyEntryRepositoryPort.findByIdAndUserId(requestDto.entryId(), userId)
                .orElseThrow(() -> new EntryNotFoundException(requestDto.entryId()));

        SleepLog sleepLog = sleepLogRepositoryPort.findByHabitIdAndEntryId(requestDto.habitId(), requestDto.entryId())
                .map(existing -> existing.update(
                        requestDto.hours(),
                        requestDto.quality(),
                        requestDto.napped(),
                        requestDto.napHours(),
                        requestDto.napNeeded()
                ))
                .orElseGet(() -> SleepLog.create(
                        requestDto.habitId(),
                        requestDto.entryId(),
                        requestDto.hours(),
                        requestDto.quality(),
                        requestDto.napped(),
                        requestDto.napHours(),
                        requestDto.napNeeded()));

        SleepLog savedLog = sleepLogRepositoryPort.save(sleepLog);
        return SleepLogMapper.toResponse(savedLog);
    }
}
