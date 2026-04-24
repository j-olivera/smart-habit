package com.smart.smart_backend.infrastructure.repository;

import com.smart.smart_backend.infrastructure.model.ExerciseLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaExerciseLogRepository extends JpaRepository<ExerciseLogEntity, Long> {
    Optional<ExerciseLogEntity> findByHabitIdAndEntryId(Long habitId, Long entryId);
    List<ExerciseLogEntity> findAllByEntryIdIn(List<Long> entryIds);
}
