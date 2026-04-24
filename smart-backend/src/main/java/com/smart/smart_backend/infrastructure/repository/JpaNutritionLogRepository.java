package com.smart.smart_backend.infrastructure.repository;

import com.smart.smart_backend.infrastructure.model.NutritionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaNutritionLogRepository extends JpaRepository<NutritionLogEntity, Long> {
    Optional<NutritionLogEntity> findByHabitIdAndEntryId(Long habitId, Long entryId);
    List<NutritionLogEntity> findAllByEntryIdIn(List<Long> entryIds);
}
