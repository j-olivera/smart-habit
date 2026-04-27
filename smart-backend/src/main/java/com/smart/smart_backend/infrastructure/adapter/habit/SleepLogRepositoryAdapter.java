package com.smart.smart_backend.infrastructure.adapter.habit;

import com.smart.smart_backend.application.port.out.logs.SleepLogRepositoryPort;
import com.smart.smart_backend.domain.model.habit.SleepLog;
import com.smart.smart_backend.infrastructure.mapper.habit.SleepLogEntityMapper;
import com.smart.smart_backend.infrastructure.repository.habit.JpaSleepLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SleepLogRepositoryAdapter implements SleepLogRepositoryPort {

    private final JpaSleepLogRepository jpaSleepLogRepository;
    private final SleepLogEntityMapper sleepLogEntityMapper;

    @Override
    public SleepLog save(SleepLog sleepLog) {
        var entity = sleepLogEntityMapper.toEntity(sleepLog);
        var saved = jpaSleepLogRepository.save(entity);
        return sleepLogEntityMapper.toDomain(saved);
    }

    @Override
    public boolean existByHabitIdAndEntryId(Long habitId, Long entryId) {
        return jpaSleepLogRepository.findByHabitIdAndEntryId(habitId, entryId).isPresent();
    }

    @Override
    public Optional<SleepLog> findByHabitIdAndEntryId(Long habitId, Long entryId) {
        return jpaSleepLogRepository.findByHabitIdAndEntryId(habitId, entryId)
                .map(sleepLogEntityMapper::toDomain);
    }
}
