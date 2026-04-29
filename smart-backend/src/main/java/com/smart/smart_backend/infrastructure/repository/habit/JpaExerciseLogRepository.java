package com.smart.smart_backend.infrastructure.repository.habit;

import com.smart.smart_backend.infrastructure.model.habit.ExerciseLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaExerciseLogRepository extends JpaRepository<ExerciseLogEntity, Long> {
    Optional<ExerciseLogEntity> findByEntryId(Long entryId);
    List<ExerciseLogEntity> findAllByEntryIdIn(List<Long> entryIds);
}
