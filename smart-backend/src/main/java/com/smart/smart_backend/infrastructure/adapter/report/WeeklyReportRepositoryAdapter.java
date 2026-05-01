package com.smart.smart_backend.infrastructure.adapter.report;

import com.smart.smart_backend.application.port.out.report.WeeklyReportRepositoryPort;
import com.smart.smart_backend.domain.model.report.WeeklyReport;
import com.smart.smart_backend.infrastructure.model.report.WeeklyReportJpaEntity;
import com.smart.smart_backend.infrastructure.repository.report.JpaWeeklyReportRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<WeeklyReport> findAllByUserIdOrderByWeekStartDesc(Long userId) {
        return repository.findByUserIdOrderByWeekStartDesc(userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private WeeklyReport toDomain(WeeklyReportJpaEntity entity) {
        return new WeeklyReport(
                entity.getId(),
                entity.getUserId(),
                entity.getWeekStart(),
                entity.getWeekEnd(),
                entity.getAiContent(),
                entity.getGeneratedAt()
        );
    }

    private WeeklyReportJpaEntity toEntity(WeeklyReport report) {
        if (report.getId() != null) {
            return new WeeklyReportJpaEntity(
                    report.getUserId(),
                    report.getWeekStart(),
                    report.getWeekEnd(),
                    report.getAiContent(),
                    report.getGeneratedAt() != null ? report.getGeneratedAt() : Instant.now()
            );
        }
        return new WeeklyReportJpaEntity(
                report.getUserId(),
                report.getWeekStart(),
                report.getWeekEnd(),
                report.getAiContent(),
                report.getGeneratedAt() != null ? report.getGeneratedAt() : Instant.now()
        );
    }
}