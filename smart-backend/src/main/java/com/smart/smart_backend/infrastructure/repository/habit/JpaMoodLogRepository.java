package com.smart.smart_backend.infrastructure.repository.habit;

import com.smart.smart_backend.infrastructure.model.habit.MoodLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaMoodLogRepository extends JpaRepository<MoodLogEntity, Long> {
    Optional<MoodLogEntity> findByEntryId(Long entryId);
    List<MoodLogEntity> findAllByEntryIdIn(List<Long> entryIds);
}
