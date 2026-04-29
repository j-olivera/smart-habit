package com.smart.smart_backend.infrastructure.repository.habit;

import com.smart.smart_backend.infrastructure.model.habit.NutritionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaNutritionLogRepository extends JpaRepository<NutritionLogEntity, Long> {
    Optional<NutritionLogEntity> findByEntryId(Long entryId);
    List<NutritionLogEntity> findAllByEntryIdIn(List<Long> entryIds);
}
