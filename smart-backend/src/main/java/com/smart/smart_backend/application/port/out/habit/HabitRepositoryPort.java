package com.smart.smart_backend.application.port.out.habit;

import com.smart.smart_backend.domain.model.habit.Habit;

import java.util.List;
import java.util.Optional;

public interface HabitRepositoryPort {
    Habit findById(Long id);
    Habit saveHabit(Habit habit, Long userId);
    boolean existsById(Long id);
    void desactivateHabit(Long id, Long userId);
    List<Habit> findAllByUserId(Long id);
    Optional<Habit> findByIdAndUserId(Long id, Long userId);
}
