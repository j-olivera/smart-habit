package com.smart.smart_backend.application.usecase.habit;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;
import com.smart.smart_backend.application.dto.habit.WeeklyEntriesReportDto;
import com.smart.smart_backend.application.port.in.habit.GetWeeklyEntriesUseCase;
import com.smart.smart_backend.application.port.out.habit.DailyEntryRepositoryPort;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class GetWeeklyEntriesService implements GetWeeklyEntriesUseCase {

    private final DailyEntryRepositoryPort dailyEntryRepositoryPort;

    public GetWeeklyEntriesService(DailyEntryRepositoryPort dailyEntryRepositoryPort) {
        this.dailyEntryRepositoryPort = dailyEntryRepositoryPort;
    }

    @Override
    public WeeklyEntriesReportDto execute(Long userId, LocalDate weekStart) {
        // Normalización: Lunes de esa semana
        LocalDate normalizedStart = weekStart.with(DayOfWeek.MONDAY);
        LocalDate normalizedEnd = normalizedStart.plusDays(6); // Domingo

        List<DailyEntryWithLogsResult> entries = dailyEntryRepositoryPort
                .findWeeklyEntriesWithLogs(userId, normalizedStart, normalizedEnd);

        return WeeklyEntriesReportDto.builder()
                .userId(userId)
                .weekStart(normalizedStart)
                .weekEnd(normalizedEnd)
                .dailyEntries(entries)
                .build();
    }
}
