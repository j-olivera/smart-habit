package com.smart.smart_backend.infrastructure.repository.habit;

import com.smart.smart_backend.infrastructure.model.habit.DailyEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaDailyEntryRepository extends JpaRepository<DailyEntryEntity, Long> {
    Optional<DailyEntryEntity> findByUserIdAndDate(Long userId, LocalDate date);
    Optional<DailyEntryEntity> findByIdAndUserId(Long id, Long userId);
    List<DailyEntryEntity> findAllByUserIdAndDateBetweenOrderByDateAsc(Long userId, LocalDate startDate, LocalDate endDate);
}
