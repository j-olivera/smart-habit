package com.smart.smart_backend.application.port.in.report;

import com.smart.smart_backend.application.dto.report.WeeklyReportResult;

public interface GenerateWeeklyReportPort {
    WeeklyReportResult execute(GenerateWeeklyReportCommand command);
}