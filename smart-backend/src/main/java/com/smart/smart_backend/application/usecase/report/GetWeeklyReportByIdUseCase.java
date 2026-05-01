package com.smart.smart_backend.application.usecase.report;

import com.smart.smart_backend.application.dto.report.WeeklyReportResult;
import com.smart.smart_backend.application.port.in.report.GetWeeklyReportByIdPort;
import com.smart.smart_backend.application.port.out.report.WeeklyReportRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class GetWeeklyReportByIdUseCase implements GetWeeklyReportByIdPort {

    private final WeeklyReportRepositoryPort reportRepo;

    @Override
    public Optional<WeeklyReportResult> execute(Long userId, Long reportId) {
        return reportRepo.findAllByUserIdOrderByWeekStartDesc(userId).stream()
                .filter(report -> report.getId().equals(reportId))
                .map(report -> new WeeklyReportResult(
                        report.getId(),
                        report.getWeekStart(),
                        report.getWeekEnd(),
                        report.getAiContent(),
                        report.getGeneratedAt()
                ))
                .findFirst();
    }
}
