package com.smart.smart_backend.infrastructure.adapter.habit;

import com.smart.smart_backend.application.port.out.logs.ExerciseLogRepositoryPort;
import com.smart.smart_backend.domain.model.habit.ExerciseLog;
import com.smart.smart_backend.infrastructure.mapper.habit.ExerciseLogEntityMapper;
import com.smart.smart_backend.infrastructure.repository.habit.JpaExerciseLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExerciseLogRepositoryAdapter implements ExerciseLogRepositoryPort {

    private final JpaExerciseLogRepository jpaExerciseLogRepository;
    private final ExerciseLogEntityMapper exerciseLogEntityMapper;

    @Override
    public ExerciseLog save(ExerciseLog exerciseLog) {
        var entity = exerciseLogEntityMapper.toEntity(exerciseLog);
        var saved = jpaExerciseLogRepository.save(entity);
        return exerciseLogEntityMapper.toDomain(saved);
    }

    @Override
    public boolean existByEntryId(Long entryId) {
        return jpaExerciseLogRepository.findByEntryId(entryId).isPresent();
    }

    @Override
    public Optional<ExerciseLog> findByEntryId(Long entryId) {
        return jpaExerciseLogRepository.findByEntryId(entryId)
                .map(exerciseLogEntityMapper::toDomain);
    }
}
