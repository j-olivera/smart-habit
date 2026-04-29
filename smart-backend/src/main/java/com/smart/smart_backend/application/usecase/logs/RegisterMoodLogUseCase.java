package com.smart.smart_backend.application.usecase.logs;

import com.smart.smart_backend.application.dto.habit.log.MoodLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.MoodLogResponseDto;
import com.smart.smart_backend.application.mapper.logs.MoodLogMapper;
import com.smart.smart_backend.application.port.in.logs.RegisterMoodLog;
import com.smart.smart_backend.application.port.out.habit.HabitRepositoryPort;
import com.smart.smart_backend.application.port.out.logs.MoodLogRepositoryPort;
import com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort;
import com.smart.smart_backend.domain.exception.DuplicateHabitLogException;
import com.smart.smart_backend.domain.exception.EntryNotFoundException;
import com.smart.smart_backend.domain.model.habit.MoodLog;

public class RegisterMoodLogUseCase implements RegisterMoodLog {

    private final DailyEntryRepositoryPort dailyEntryRepositoryPort;
    private final MoodLogRepositoryPort moodLogRepositoryPort;

    public RegisterMoodLogUseCase(DailyEntryRepositoryPort dailyEntryRepositoryPort, MoodLogRepositoryPort moodLogRepositoryPort) {
        this.dailyEntryRepositoryPort = dailyEntryRepositoryPort;
        this.moodLogRepositoryPort = moodLogRepositoryPort;
    }

    @Override
    public MoodLogResponseDto execute(Long userId, MoodLogRequestDto requestDto) {

        dailyEntryRepositoryPort.findByIdAndUserId(requestDto.entryId(), userId)
                .orElseThrow(() -> new EntryNotFoundException(requestDto.entryId()));

        MoodLog moodLog = moodLogRepositoryPort.findByEntryId(requestDto.entryId())
                .map(existing -> existing.update(
                        requestDto.mood(),
                        requestDto.hasObservations(),
                        requestDto.eventDescription(),
                        requestDto.socialized(),
                        requestDto.socialWith()
                ))
                .orElseGet(() -> MoodLog.create(
                        requestDto.entryId(),
                        requestDto.mood(),
                        requestDto.hasObservations(),
                        requestDto.eventDescription(),
                        requestDto.socialized(),
                        requestDto.socialWith()));

        MoodLog savedLog = moodLogRepositoryPort.save(moodLog);
        return MoodLogMapper.toResponse(savedLog);
    }
}
