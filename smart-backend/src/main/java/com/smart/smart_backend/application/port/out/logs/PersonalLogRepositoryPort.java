package com.smart.smart_backend.application.port.out.logs;

import com.smart.smart_backend.domain.model.habit.PersonalLog;

import java.util.Optional;

public interface PersonalLogRepositoryPort {
    PersonalLog save(PersonalLog personalLog);
    boolean existByHabitIdAndEntryId(Long habitId, Long entryId);
    Optional<PersonalLog> findByHabitIdAndEntryId(Long habitId, Long entryId);
}
