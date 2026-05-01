package com.smart.smart_backend.infrastructure.adapter.report;

import com.smart.smart_backend.application.dto.report.WeeklyReportSummary;
import com.smart.smart_backend.application.port.out.report.WeeklyReportRepositoryPort;
import com.smart.smart_backend.domain.model.report.WeeklyReport;
import com.smart.smart_backend.infrastructure.model.report.WeeklyReportJpaEntity;
import com.smart.smart_backend.infrastructure.repository.report.JpaWeeklyReportRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class WeeklyReportRepositoryAdapter implements WeeklyReportRepositoryPort {

    private final JpaWeeklyReportRepository repository;

    public WeeklyReportRepositoryAdapter(JpaWeeklyReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<WeeklyReport> findByUserIdAndWeekStart(Long userId, LocalDate weekStart) {
        return repository.findByUserIdAndWeekStart(userId, weekStart)
                .map(this::toDomain);
    }

    @Override
    public WeeklyReport save(WeeklyReport report) {
        WeeklyReportJpaEntity entity = toEntity(report);
        WeeklyReportJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<WeeklyReportSummary> findSummariesByUserId(Long userId) {
        // Delegamos la proyección directamente a la DB para mayor eficiencia
        return repository.findSummariesByUserId(userId);
    }

    @Override
    public Optional<WeeklyReport> findByIdAndUserId(Long id, Long userId) {
        // Búsqueda directa indexada, mucho más rápida que filtrar en memoria
        return repository.findByIdAndUserId(id, userId)
                .map(this::toDomain);
    }

    private WeeklyReport toDomain(WeeklyReportJpaEntity entity) {
        return new WeeklyReport(
                entity.getId(),
                entity.getUserId(),
                entity.getWeekStart(),
                entity.getWeekEnd(),
                entity.getAiContent(),
                entity.getGeneratedAt());
    }

    private WeeklyReportJpaEntity toEntity(WeeklyReport report) {
        if (report.getId() != null) {
            return new WeeklyReportJpaEntity(
                    report.getUserId(),
                    report.getWeekStart(),
                    report.getWeekEnd(),
                    report.getAiContent(),
                    report.getGeneratedAt() != null ? report.getGeneratedAt() : Instant.now());
        }
        return new WeeklyReportJpaEntity(
                report.getUserId(),
                report.getWeekStart(),
                report.getWeekEnd(),
                report.getAiContent(),
                report.getGeneratedAt() != null ? report.getGeneratedAt() : Instant.now());
    }
}