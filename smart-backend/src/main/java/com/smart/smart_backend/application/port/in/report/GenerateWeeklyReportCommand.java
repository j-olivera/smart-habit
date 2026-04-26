package com.smart.smart_backend.application.port.in.report;

import java.time.LocalDate;

public record GenerateWeeklyReportCommand(
    Long userId,
    LocalDate weekStart,
    String triggeredBy
) {}