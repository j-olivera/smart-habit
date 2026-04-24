package com.smart.smart_backend.application.port.in.habit;

import com.smart.smart_backend.application.dto.habit.WeeklyEntriesReportDto;

import java.time.LocalDate;

public interface GetWeeklyEntriesUseCase {
    WeeklyEntriesReportDto execute(Long userId, LocalDate weekStart);
}
