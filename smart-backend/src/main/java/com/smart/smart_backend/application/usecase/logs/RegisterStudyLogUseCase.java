package com.smart.smart_backend.application.usecase.logs;

import com.smart.smart_backend.application.dto.habit.log.StudyLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.StudyLogResponseDto;
import com.smart.smart_backend.application.mapper.logs.StudyLogMapper;
import com.smart.smart_backend.application.port.in.logs.RegisterStudyLog;
import com.smart.smart_backend.application.port.out.logs.StudyLogRepositoryPort;
import com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort;
import com.smart.smart_backend.application.port.out.habit.HabitRepositoryPort;
import com.smart.smart_backend.domain.enums.HabitType;
import com.smart.smart_backend.domain.exception.DuplicateHabitLogException;
import com.smart.smart_backend.domain.exception.EntryNotFoundException;
import com.smart.smart_backend.domain.exception.HabitTypeMisMatchException;
import com.smart.smart_backend.domain.model.habit.Habit;
import com.smart.smart_backend.domain.exception.HabitNotFoundException;
import com.smart.smart_backend.domain.model.habit.StudyLog;

public class RegisterStudyLogUseCase implements RegisterStudyLog {

    private final HabitRepositoryPort habitRepositoryPort;
    private final DailyEntryRepositoryPort dailyEntryRepositoryPort;
    private final StudyLogRepositoryPort studyLogRepositoryPort;

    public RegisterStudyLogUseCase(HabitRepositoryPort habitRepositoryPort,
            DailyEntryRepositoryPort dailyEntryRepositoryPort, StudyLogRepositoryPort studyLogRepositoryPort) {
        this.habitRepositoryPort = habitRepositoryPort;
        this.dailyEntryRepositoryPort = dailyEntryRepositoryPort;
        this.studyLogRepositoryPort = studyLogRepositoryPort;
    }

    @Override
    public StudyLogResponseDto execute(Long userId, StudyLogRequestDto requestDto) {

        Habit habit = habitRepositoryPort.findByIdAndUserId(requestDto.habitId(), userId)
                .orElseThrow(()-> new HabitNotFoundException(requestDto.habitId()));

        if(habit.getType() != HabitType.STUDY){
            throw new HabitTypeMisMatchException(habit.getType(), HabitType.STUDY);
        }

        dailyEntryRepositoryPort.findByIdAndUserId(requestDto.entryId(), userId)
                .orElseThrow(() -> new EntryNotFoundException(requestDto.entryId()));

        if(studyLogRepositoryPort.existByHabitIdAndEntryId(requestDto.habitId(), requestDto.entryId())){
            throw new DuplicateHabitLogException(requestDto.habitId(), requestDto.entryId());
        }

        StudyLog studyLog = StudyLog.create(
                requestDto.habitId(),
                requestDto.entryId(),
                requestDto.studied(),
                requestDto.hours(),
                requestDto.subject(),
                requestDto.skipReason()
        );

        StudyLog save = studyLogRepositoryPort.save(studyLog);
        return StudyLogMapper.toResponse(save);
    }

}
