package com.smart.smart_backend.application.port.out.logs;

import com.smart.smart_backend.domain.model.habit.StudyLog;

import java.util.Optional;

public interface StudyLogRepositoryPort {
    boolean existByEntryId(Long entryId);
    Optional<StudyLog> findByEntryId(Long entryId); //cambio
    StudyLog save(StudyLog studyLog);
}