package com.smart.smart_backend.application.port.out.logs;

import com.smart.smart_backend.domain.model.habit.MoodLog;

import java.util.Optional;

public interface MoodLogRepositoryPort {
    MoodLog save(MoodLog moodLog);
    boolean existByHabitIdAndEntryId(Long habitId, Long entryId);
    Optional<MoodLog> findByHabitIdAndEntryId(Long habitId, Long entryId);
}
