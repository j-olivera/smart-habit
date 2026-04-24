package com.smart.smart_backend.application.port.out.logs;

import com.smart.smart_backend.domain.model.habit.ExerciseLog;

import java.util.Optional;

public interface ExerciseLogRepositoryPort {
    ExerciseLog save(ExerciseLog exerciseLog);
    boolean existByHabitIdAndEntryId(Long habitId, Long entryId);
    Optional<ExerciseLog> findByHabitIdAndEntryId(Long habitId, Long entryId);
}
