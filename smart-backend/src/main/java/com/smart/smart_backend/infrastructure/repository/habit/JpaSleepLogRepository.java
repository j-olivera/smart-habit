package com.smart.smart_backend.infrastructure.repository.habit;

import com.smart.smart_backend.infrastructure.model.habit.SleepLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaSleepLogRepository extends JpaRepository<SleepLogEntity, Long> {
    Optional<SleepLogEntity> findByHabitIdAndEntryId(Long habitId, Long entryId);
    List<SleepLogEntity> findAllByEntryIdIn(List<Long> entryIds);
}
