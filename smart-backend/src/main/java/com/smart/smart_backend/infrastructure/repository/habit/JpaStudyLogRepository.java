package com.smart.smart_backend.infrastructure.repository.habit;

import com.smart.smart_backend.infrastructure.model.habit.StudyLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaStudyLogRepository extends JpaRepository<StudyLogEntity, Long> {
    Optional<StudyLogEntity> findByEntryId(Long entryId);
    List<StudyLogEntity> findAllByEntryIdIn(List<Long> entryIds);
}
