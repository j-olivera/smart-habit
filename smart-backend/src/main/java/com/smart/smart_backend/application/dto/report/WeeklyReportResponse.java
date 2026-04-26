package com.smart.smart_backend.application.dto.report;

import java.time.Instant;
import java.time.LocalDate;

public record WeeklyReportResponse(
    Long id,
    LocalDate weekStart,
    LocalDate weekEnd,
    String aiContent,
    Instant generatedAt
) {
    public static WeeklyReportResponse from(WeeklyReportResult result) {
        return new WeeklyReportResponse(
            result.id(),
            result.weekStart(),
            result.weekEnd(),
            result.aiContent(),
            result.generatedAt()
        );
    }
}