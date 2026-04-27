package com.smart.smart_backend.infrastructure.adapter.habit;

import com.smart.smart_backend.application.port.out.logs.NutritionLogRepositoryPort;
import com.smart.smart_backend.domain.model.habit.NutritionLog;
import com.smart.smart_backend.infrastructure.mapper.habit.NutritionLogEntityMapper;
import com.smart.smart_backend.infrastructure.repository.habit.JpaNutritionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NutritionLogRepositoryAdapter implements NutritionLogRepositoryPort {

    private final JpaNutritionLogRepository jpaNutritionLogRepository;
    private final NutritionLogEntityMapper nutritionLogEntityMapper;

    @Override
    public NutritionLog save(NutritionLog nutritionLog) {
        var entity = nutritionLogEntityMapper.toEntity(nutritionLog);
        var saved = jpaNutritionLogRepository.save(entity);
        return nutritionLogEntityMapper.toDomain(saved);
    }

    @Override
    public boolean existByHabitIdAndEntryId(Long habitId, Long entryId) {
        return jpaNutritionLogRepository.findByHabitIdAndEntryId(habitId, entryId).isPresent();
    }

    @Override
    public Optional<NutritionLog> findByHabitIdAndEntryId(Long habitId, Long entryId) {
        return jpaNutritionLogRepository.findByHabitIdAndEntryId(habitId, entryId)
                .map(nutritionLogEntityMapper::toDomain);
    }
}
