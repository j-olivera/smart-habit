package com.smart.smart_backend.application.port.in.report;

import com.smart.smart_backend.application.dto.report.WeeklyReportResult;
import java.util.Optional;

public interface GetWeeklyReportByIdPort {
    Optional<WeeklyReportResult> execute(Long userId, Long reportId);
}
