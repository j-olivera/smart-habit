package com.smart.smart_backend.infrastructure.adapter.habit;

import com.smart.smart_backend.application.port.out.logs.PersonalLogRepositoryPort;
import com.smart.smart_backend.domain.model.habit.PersonalLog;
import com.smart.smart_backend.infrastructure.mapper.habit.PersonalLogEntityMapper;
import com.smart.smart_backend.infrastructure.repository.habit.JpaPersonalLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PersonalLogRepositoryAdapter implements PersonalLogRepositoryPort {

    private final JpaPersonalLogRepository jpaPersonalLogRepository;
    private final PersonalLogEntityMapper personalLogEntityMapper;

    @Override
    public boolean existByHabitIdAndEntryId(Long habitId, Long entryId) {
        return jpaPersonalLogRepository.findByHabitIdAndEntryId(habitId, entryId).isPresent();
    }

    @Override
    public Optional<PersonalLog> findByHabitIdAndEntryId(Long habitId, Long entryId) {
        return jpaPersonalLogRepository.findByHabitIdAndEntryId(habitId, entryId)
                .map(personalLogEntityMapper::toDomain);
    }

    @Override
    public PersonalLog save(PersonalLog personalLog) {
        var entity = personalLogEntityMapper.toEntity(personalLog);
        var saved = jpaPersonalLogRepository.save(entity);
        return personalLogEntityMapper.toDomain(saved);
    }
}
