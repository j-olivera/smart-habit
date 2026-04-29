package com.smart.smart_backend.application.port.out.logs;

import com.smart.smart_backend.domain.model.habit.ExerciseLog;

import java.util.Optional;

public interface ExerciseLogRepositoryPort {
    ExerciseLog save(ExerciseLog exerciseLog);
    boolean existByEntryId(Long entryId);
    Optional<ExerciseLog> findByEntryId(Long entryId);
}
