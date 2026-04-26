package com.smart.smart_backend.application.dto.report;

import java.time.Instant;
import java.time.LocalDate;

public record WeeklyReportResult(
    Long id,
    LocalDate weekStart,
    LocalDate weekEnd,
    String aiContent,
    Instant generatedAt
) {}