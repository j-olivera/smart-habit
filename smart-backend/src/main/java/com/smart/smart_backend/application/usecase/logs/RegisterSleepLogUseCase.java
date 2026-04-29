package com.smart.smart_backend.application.usecase.logs;

import com.smart.smart_backend.application.dto.habit.log.SleepLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.SleepLogResponseDto;
import com.smart.smart_backend.application.mapper.logs.SleepLogMapper;
import com.smart.smart_backend.application.port.in.logs.RegisterSleepLog;
import com.smart.smart_backend.application.port.out.habit.HabitRepositoryPort;
import com.smart.smart_backend.application.port.out.logs.SleepLogRepositoryPort;
import com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort;
import com.smart.smart_backend.domain.exception.DuplicateHabitLogException;
import com.smart.smart_backend.domain.exception.EntryNotFoundException;
import com.smart.smart_backend.domain.model.habit.SleepLog;

public class RegisterSleepLogUseCase implements RegisterSleepLog {

    private final DailyEntryRepositoryPort dailyEntryRepositoryPort;
    private final SleepLogRepositoryPort sleepLogRepositoryPort;

    public RegisterSleepLogUseCase(DailyEntryRepositoryPort dailyEntryRepositoryPort, SleepLogRepositoryPort sleepLogRepositoryPort) {
        this.dailyEntryRepositoryPort = dailyEntryRepositoryPort;
        this.sleepLogRepositoryPort = sleepLogRepositoryPort;
    }

    @Override
    public SleepLogResponseDto execute(Long userId, SleepLogRequestDto requestDto) {

        dailyEntryRepositoryPort.findByIdAndUserId(requestDto.entryId(), userId)
                .orElseThrow(() -> new EntryNotFoundException(requestDto.entryId()));

        SleepLog sleepLog = sleepLogRepositoryPort.findByEntryId(requestDto.entryId())
                .map(existing -> existing.update(
                        requestDto.hours(),
                        requestDto.quality(),
                        requestDto.napped(),
                        requestDto.napHours(),
                        requestDto.napNeeded()
                ))
                .orElseGet(() -> SleepLog.create(
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
