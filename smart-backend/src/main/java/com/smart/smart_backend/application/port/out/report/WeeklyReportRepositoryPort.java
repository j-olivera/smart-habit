package com.smart.smart_backend.application.port.out.report;

import com.smart.smart_backend.application.dto.report.WeeklyReportSummary;
import com.smart.smart_backend.domain.model.report.WeeklyReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeeklyReportRepositoryPort {
    Optional<WeeklyReport> findByUserIdAndWeekStart(Long userId, LocalDate weekStart);
    WeeklyReport save(WeeklyReport report);

    // Optimización: Retorna resúmenes livianos para evitar cargar contenido pesado en listados
    List<WeeklyReportSummary> findSummariesByUserId(Long userId);

    // Optimización: Recupera un reporte específico directamente por ID y Usuario
    Optional<WeeklyReport> findByIdAndUserId(Long id, Long userId);
}