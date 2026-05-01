package com.smart.smart_backend.application.dto.report;

import com.smart.smart_backend.domain.model.report.WeeklyReport;
import java.time.Instant;
import java.time.LocalDate;

public record WeeklyReportSummary(
    Long id,
    LocalDate weekStart,
    LocalDate weekEnd,
    Instant generatedAt
) {
    public static WeeklyReportSummary from(WeeklyReport report) {
        return new WeeklyReportSummary(
            report.getId(),
            report.getWeekStart(),
            report.getWeekEnd(),
            report.getGeneratedAt()
        );
    }
}
