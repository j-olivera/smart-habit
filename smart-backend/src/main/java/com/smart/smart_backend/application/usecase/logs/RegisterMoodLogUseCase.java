package com.smart.smart_backend.application.usecase.logs;

import com.smart.smart_backend.application.dto.habit.log.MoodLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.MoodLogResponseDto;
import com.smart.smart_backend.application.mapper.logs.MoodLogMapper;
import com.smart.smart_backend.application.port.in.logs.RegisterMoodLog;
import com.smart.smart_backend.application.port.out.habit.HabitRepositoryPort;
import com.smart.smart_backend.application.port.out.logs.MoodLogRepositoryPort;
import com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort;
import com.smart.smart_backend.domain.enums.HabitType;
import com.smart.smart_backend.domain.exception.DuplicateHabitLogException;
import com.smart.smart_backend.domain.exception.EntryNotFoundException;
import com.smart.smart_backend.domain.exception.HabitNotFoundException;
import com.smart.smart_backend.domain.exception.HabitTypeMisMatchException;
import com.smart.smart_backend.domain.model.habit.Habit;
import com.smart.smart_backend.domain.model.habit.MoodLog;

public class RegisterMoodLogUseCase implements RegisterMoodLog {

    private final HabitRepositoryPort habitRepositoryPort;
    private final DailyEntryRepositoryPort dailyEntryRepositoryPort;
    private final MoodLogRepositoryPort moodLogRepositoryPort;

    public RegisterMoodLogUseCase(HabitRepositoryPort habitRepositoryPort,
            DailyEntryRepositoryPort dailyEntryRepositoryPort, MoodLogRepositoryPort moodLogRepositoryPort) {
        this.habitRepositoryPort = habitRepositoryPort;
        this.dailyEntryRepositoryPort = dailyEntryRepositoryPort;
        this.moodLogRepositoryPort = moodLogRepositoryPort;
    }

    @Override
    public MoodLogResponseDto execute(Long userId, MoodLogRequestDto requestDto) {

        Habit habit = habitRepositoryPort.findByIdAndUserId(requestDto.habitId(), userId)
                .orElseThrow(() -> new HabitNotFoundException(requestDto.habitId()));

        if (habit.getType() != HabitType.MOOD) {
            throw new HabitTypeMisMatchException(habit.getType(), HabitType.MOOD);
        }

        dailyEntryRepositoryPort.findByIdAndUserId(requestDto.entryId(), userId)
                .orElseThrow(() -> new EntryNotFoundException(requestDto.entryId()));

        if (moodLogRepositoryPort.existByHabitIdAndEntryId(requestDto.habitId(), requestDto.entryId())) {
            throw new DuplicateHabitLogException(requestDto.habitId(), requestDto.entryId());
        }

        MoodLog moodLog = MoodLog.create(
                requestDto.habitId(),
                requestDto.entryId(),
                requestDto.mood(),
                requestDto.hasObservations(),
                requestDto.eventDescription(),
                requestDto.socialized(),
                requestDto.socialWith());

        MoodLog savedLog = moodLogRepositoryPort.save(moodLog);
        return MoodLogMapper.toResponse(savedLog);
    }
}
