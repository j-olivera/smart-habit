package com.smart.smart_backend.infrastructure.adapter.habit;

import com.smart.smart_backend.application.port.out.logs.MoodLogRepositoryPort;
import com.smart.smart_backend.domain.model.habit.MoodLog;
import com.smart.smart_backend.infrastructure.mapper.habit.MoodLogEntityMapper;
import com.smart.smart_backend.infrastructure.repository.habit.JpaMoodLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MoodLogRepositoryAdapter implements MoodLogRepositoryPort {

    private final JpaMoodLogRepository jpaMoodLogRepository;
    private final MoodLogEntityMapper moodLogEntityMapper;

    @Override
    public MoodLog save(MoodLog moodLog) {
        var entity = moodLogEntityMapper.toEntity(moodLog);
        var saved = jpaMoodLogRepository.save(entity);
        return moodLogEntityMapper.toDomain(saved);
    }

    @Override
    public boolean existByEntryId(Long entryId) {
        return jpaMoodLogRepository.findByEntryId(entryId).isPresent();
    }

    @Override
    public Optional<MoodLog> findByEntryId(Long entryId) {
        return jpaMoodLogRepository.findByEntryId(entryId)
                .map(moodLogEntityMapper::toDomain);
    }
}
