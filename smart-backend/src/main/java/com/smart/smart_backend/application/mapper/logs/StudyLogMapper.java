package com.smart.smart_backend.application.mapper.logs;

import com.smart.smart_backend.application.dto.habit.log.StudyLogResponseDto;
import com.smart.smart_backend.domain.model.habit.StudyLog;

public class StudyLogMapper {
    public static StudyLog toEntity(StudyLogResponseDto dto) {
        return StudyLog.create(
                dto.habitId(),
                dto.entryId(),
                dto.studied(),
                dto.hours(),
                dto.subject(),
                dto.skipReason());
    }

    public static StudyLogResponseDto toResponse(StudyLog studyLog) {
        return new StudyLogResponseDto(
                studyLog.getId(),
                studyLog.getHabitId(),
                studyLog.getEntryId(),
                studyLog.isStudied(),
                studyLog.getHours(),
                studyLog.getSubject(),
                studyLog.getSkipReason());
    }
}
