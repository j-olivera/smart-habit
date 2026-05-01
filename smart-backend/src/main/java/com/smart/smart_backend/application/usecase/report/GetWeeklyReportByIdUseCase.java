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
        // Optimización: Reemplazamos el filtro en memoria (.stream().filter()) 
        // por una consulta directa a la base de datos que ya filtra por IDs.
        return reportRepo.findByIdAndUserId(reportId, userId)
                .map(report -> new WeeklyReportResult(
                        report.getId(),
                        report.getWeekStart(),
                        report.getWeekEnd(),
                        report.getAiContent(),
                        report.getGeneratedAt()
                ));
    }
}
