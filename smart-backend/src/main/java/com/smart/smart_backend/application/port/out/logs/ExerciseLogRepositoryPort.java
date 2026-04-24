package com.smart.smart_backend.application.port.out.logs;

import com.smart.smart_backend.domain.model.habit.ExerciseLog;

public interface ExerciseLogRepositoryPort {
    ExerciseLog save(ExerciseLog exerciseLog);
    boolean existByHabitIdAndEntryId(Long habitId, Long entryId);
}
