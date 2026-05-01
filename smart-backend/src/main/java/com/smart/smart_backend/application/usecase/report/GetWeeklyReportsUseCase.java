package com.smart.smart_backend.application.usecase.report;

import com.smart.smart_backend.application.dto.report.WeeklyReportSummary;
import com.smart.smart_backend.application.port.in.report.GetWeeklyReportsPort;
import com.smart.smart_backend.application.port.out.report.WeeklyReportRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetWeeklyReportsUseCase implements GetWeeklyReportsPort {

    private final WeeklyReportRepositoryPort reportRepo;

    @Override
    public List<WeeklyReportSummary> execute(Long userId) {
        // Optimización: Usamos el método de proyección del repositorio.
        // Esto evita traer la columna 'ai_content' (TEXT) de todos los registros a la
        // RAM.
        return reportRepo.findSummariesByUserId(userId);
    }
}
