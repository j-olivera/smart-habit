package com.smart.smart_backend.infrastructure.adapter.habit;

import com.smart.smart_backend.application.port.out.logs.StudyLogRepositoryPort;
import com.smart.smart_backend.domain.model.habit.StudyLog;
import com.smart.smart_backend.infrastructure.mapper.habit.StudyLogEntityMapper;
import com.smart.smart_backend.infrastructure.repository.habit.JpaStudyLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StudyLogRepositoryAdapter implements StudyLogRepositoryPort {

    private final JpaStudyLogRepository jpaStudyLogRepository;
    private final StudyLogEntityMapper studyLogEntityMapper;

    @Override
    public boolean existByHabitIdAndEntryId(Long habitId, Long entryId) {
        return jpaStudyLogRepository.findByHabitIdAndEntryId(habitId, entryId).isPresent();
    }

    @Override
    public Optional<StudyLog> findByHabitIdAndEntryId(Long habitId, Long entryId) {
        return jpaStudyLogRepository.findByHabitIdAndEntryId(habitId, entryId)
                .map(studyLogEntityMapper::toDomain);
    }

    @Override
    public StudyLog save(StudyLog studyLog) {
        var entity = studyLogEntityMapper.toEntity(studyLog);
        var saved = jpaStudyLogRepository.save(entity);
        return studyLogEntityMapper.toDomain(saved);
    }
}
