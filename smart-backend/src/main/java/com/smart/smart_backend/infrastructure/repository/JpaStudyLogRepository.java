package com.smart.smart_backend.infrastructure.repository;

import com.smart.smart_backend.infrastructure.model.StudyLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaStudyLogRepository extends JpaRepository<StudyLogEntity, Long> {
    Optional<StudyLogEntity> findByHabitIdAndEntryId(Long habitId, Long entryId);
    List<StudyLogEntity> findAllByEntryIdIn(List<Long> entryIds);
}
