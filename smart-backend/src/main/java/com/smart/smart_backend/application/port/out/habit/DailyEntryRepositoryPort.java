package com.smart.smart_backend.application.port.out.habit;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyEntryRepositoryPort {
    Optional<DailyEntryWithLogsResult> findByUserIdAndDateWithLogs(Long userId, LocalDate date);
    List<DailyEntryWithLogsResult> findWeeklyEntriesWithLogs(Long userId, LocalDate startDate, LocalDate endDate);
}
