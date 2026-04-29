package com.smart.smart_backend.infrastructure.mapper.habit;

import com.smart.smart_backend.domain.enums.MuscularGroup;
import com.smart.smart_backend.domain.model.habit.ExerciseLog;
import com.smart.smart_backend.infrastructure.model.habit.ExerciseLogEntity;
import org.springframework.stereotype.Component;

@Component
public class ExerciseLogEntityMapper {

    public ExerciseLog toDomain(ExerciseLogEntity entity) {
        if (entity == null) return null;
        return new ExerciseLog(
                entity.getId(),

                entity.getEntryId(),
                entity.getExercised(),
                entity.getHours(),
                entity.getMuscleGroups() != null ? MuscularGroup.valueOf(entity.getMuscleGroups()) : null,
                entity.getEnergyLevel(),
                entity.getSkipReason()
        );
    }

    public ExerciseLogEntity toEntity(ExerciseLog domain) {
        if (domain == null) return null;
        return ExerciseLogEntity.builder()
                .id(domain.getId())

                .entryId(domain.getEntryId())
                .exercised(domain.isExercised())
                .hours(domain.getHours())
                .muscleGroups(domain.getMuscularGroup() != null ? domain.getMuscularGroup().name() : null)
                .energyLevel(domain.getEnergyLevel())
                .skipReason(domain.getSkipReason())
                .build();
    }
}
