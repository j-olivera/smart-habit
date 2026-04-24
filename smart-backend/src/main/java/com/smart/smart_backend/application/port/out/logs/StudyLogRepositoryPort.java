package com.smart.smart_backend.application.port.out.logs;

import com.smart.smart_backend.domain.model.habit.StudyLog;

public interface StudyLogRepositoryPort {
    boolean existByHabitIdAndEntryId(Long habitId, Long entryId);

    StudyLog save(StudyLog studyLog);
}