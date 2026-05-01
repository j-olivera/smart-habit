package com.smart.smart_backend.infrastructure.repository.report;

import com.smart.smart_backend.infrastructure.model.report.WeeklyReportJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaWeeklyReportRepository extends JpaRepository<WeeklyReportJpaEntity, Long> {
    Optional<WeeklyReportJpaEntity> findByUserIdAndWeekStart(Long userId, LocalDate weekStart);

    // Optimización: Traemos solo los campos necesarios para el listado, evitando la columna pesada 'ai_content'
    @Query("SELECT new com.smart.smart_backend.application.dto.report.WeeklyReportSummary(r.id, r.weekStart, r.weekEnd, r.generatedAt) " +
           "FROM WeeklyReportJpaEntity r WHERE r.userId = :userId ORDER BY r.weekStart DESC")
    List<com.smart.smart_backend.application.dto.report.WeeklyReportSummary> findSummariesByUserId(@Param("userId") Long userId);

    // Optimización: Búsqueda directa por ID y UserID en la base de datos (O(1) indexado)
    Optional<WeeklyReportJpaEntity> findByIdAndUserId(Long id, Long userId);
}