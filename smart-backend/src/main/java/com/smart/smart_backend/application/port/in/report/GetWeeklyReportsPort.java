package com.smart.smart_backend.application.port.in.report;

import com.smart.smart_backend.application.dto.report.WeeklyReportSummary;
import java.util.List;

public interface GetWeeklyReportsPort {
    List<WeeklyReportSummary> execute(Long userId);
}
