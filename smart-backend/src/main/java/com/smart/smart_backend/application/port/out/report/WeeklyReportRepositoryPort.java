package com.smart.smart_backend.application.port.out.report;

import com.smart.smart_backend.domain.model.report.WeeklyReport;

import java.time.LocalDate;
import java.util.Optional;

public interface WeeklyReportRepositoryPort {
    Optional<WeeklyReport> findByUserIdAndWeekStart(Long userId, LocalDate weekStart);
    WeeklyReport save(WeeklyReport report);
}