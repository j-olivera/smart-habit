package com.smart.smart_backend.application.port.out.ai;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;

import java.time.LocalDate;
import java.util.List;

public interface PromptBuilderPort {
    String build(List<DailyEntryWithLogsResult> entries, LocalDate weekStart, LocalDate weekEnd);
}