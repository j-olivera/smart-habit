package com.smart.smart_backend.application.port.out.register;

import com.smart.smart_backend.domain.model.habit.DailyEntry;

import java.util.Optional;

public interface DailyEntryRepositoryPort {
    Optional<DailyEntry> findByIdAndUserId(Long id, Long userId);
}
