package com.smart.smart_backend.infrastructure.repository.habit;

import com.smart.smart_backend.infrastructure.model.habit.PersonalLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaPersonalLogRepository extends JpaRepository<PersonalLogEntity, Long> {
    Optional<PersonalLogEntity> findByHabitIdAndEntryId(Long habitId, Long entryId);
    List<PersonalLogEntity> findAllByEntryIdIn(List<Long> entryIds);
}
