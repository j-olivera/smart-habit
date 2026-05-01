package com.smart.smart_backend.infrastructure.repository.report;

import com.smart.smart_backend.infrastructure.model.report.WeeklyReportJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaWeeklyReportRepository extends JpaRepository<WeeklyReportJpaEntity, Long> {
    Optional<WeeklyReportJpaEntity> findByUserIdAndWeekStart(Long userId, LocalDate weekStart);
    List<WeeklyReportJpaEntity> findByUserIdOrderByWeekStartDesc(Long userId);
}