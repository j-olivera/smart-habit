package com.smart.smart_backend.application.port.out.logs;

import com.smart.smart_backend.domain.model.habit.SleepLog;

public interface SleepLogRepositoryPort {
    SleepLog save(SleepLog sleepLog);
    boolean existByHabitIdAndEntryId(Long habitId, Long entryId);
}
