package com.smart.smart_backend.application.usecase.logs;

import com.smart.smart_backend.application.dto.habit.log.PersonalLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.PersonalLogResponseDto;
import com.smart.smart_backend.application.mapper.logs.PersonalLogMapper;
import com.smart.smart_backend.application.port.in.logs.RegisterPersonalLog;
import com.smart.smart_backend.application.port.out.habit.HabitRepositoryPort;
import com.smart.smart_backend.application.port.out.logs.PersonalLogRepositoryPort;
import com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort;
import com.smart.smart_backend.domain.enums.HabitType;
import com.smart.smart_backend.domain.exception.EntryNotFoundException;
import com.smart.smart_backend.domain.exception.HabitNotFoundException;
import com.smart.smart_backend.domain.exception.HabitTypeMisMatchException;
import com.smart.smart_backend.domain.model.habit.Habit;
import com.smart.smart_backend.domain.model.habit.PersonalLog;

public class RegisterPersonalLogUseCase implements RegisterPersonalLog {

    private final HabitRepositoryPort habitRepositoryPort;
    private final DailyEntryRepositoryPort dailyEntryRepositoryPort;
    private final PersonalLogRepositoryPort personalLogRepositoryPort;

    public RegisterPersonalLogUseCase(HabitRepositoryPort habitRepositoryPort,
                                      DailyEntryRepositoryPort dailyEntryRepositoryPort,
                                      PersonalLogRepositoryPort personalLogRepositoryPort) {
        this.habitRepositoryPort = habitRepositoryPort;
        this.dailyEntryRepositoryPort = dailyEntryRepositoryPort;
        this.personalLogRepositoryPort = personalLogRepositoryPort;
    }

    @Override
    public PersonalLogResponseDto execute(Long userId, PersonalLogRequestDto requestDto) {

        Habit habit = habitRepositoryPort.findByIdAndUserId(requestDto.habitId(), userId)
                .orElseThrow(() -> new HabitNotFoundException(requestDto.habitId()));

        if (habit.getType() != HabitType.PERSONAL) {
            throw new HabitTypeMisMatchException(habit.getType(), HabitType.PERSONAL);
        }

        dailyEntryRepositoryPort.findByIdAndUserId(requestDto.entryId(), userId)
                .orElseThrow(() -> new EntryNotFoundException(requestDto.entryId()));

        PersonalLog log = personalLogRepositoryPort.findByHabitIdAndEntryId(requestDto.habitId(), requestDto.entryId())
                .map(existing -> existing.update(
                        requestDto.completed(),
                        requestDto.hours(),
                        requestDto.description()
                ))
                .orElseGet(() -> PersonalLog.create(
                        requestDto.habitId(),
                        requestDto.entryId(),
                        requestDto.completed(),
                        requestDto.hours(),
                        requestDto.description()
                ));

        PersonalLog savedLog = personalLogRepositoryPort.save(log);
        return PersonalLogMapper.toResponse(savedLog);
    }
}
