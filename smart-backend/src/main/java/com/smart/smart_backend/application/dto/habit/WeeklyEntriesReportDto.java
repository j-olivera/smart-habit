package com.smart.smart_backend.application.dto.habit;

import lombok.Builder;
import java.time.LocalDate;
import java.util.List;

@Builder
public record WeeklyEntriesReportDto(
    Long userId,
    LocalDate weekStart,
    LocalDate weekEnd,
    List<DailyEntryWithLogsResult> dailyEntries
) {}
