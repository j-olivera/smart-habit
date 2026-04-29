package com.smart.smart_backend.application.port.out.logs;

import com.smart.smart_backend.domain.model.habit.MoodLog;

import java.util.Optional;

public interface MoodLogRepositoryPort {
    MoodLog save(MoodLog moodLog);
    boolean existByEntryId(Long entryId);
    Optional<MoodLog> findByEntryId(Long entryId);
}
