package com.smart.smart_backend.application.port.out.habit;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;

import java.time.LocalDate;
import java.util.List;

public interface WeeklyEntriesPort {
    List<DailyEntryWithLogsResult> execute(Long userId, LocalDate weekStart, LocalDate weekEnd);
}