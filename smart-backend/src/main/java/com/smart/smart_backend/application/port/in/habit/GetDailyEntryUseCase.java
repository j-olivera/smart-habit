package com.smart.smart_backend.application.port.in.habit;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;

import java.time.LocalDate;

public interface GetDailyEntryUseCase {
    DailyEntryWithLogsResult execute(Long userId, LocalDate date);
}
