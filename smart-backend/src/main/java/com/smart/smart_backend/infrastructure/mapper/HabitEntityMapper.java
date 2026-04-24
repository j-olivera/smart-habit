package com.smart.smart_backend.infrastructure.mapper;

import com.smart.smart_backend.domain.enums.HabitType;
import com.smart.smart_backend.domain.model.habit.Habit;
import com.smart.smart_backend.infrastructure.model.HabitEntity;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.LocalDateTime;

@Component
public class HabitEntityMapper {

    public Habit toDomain(HabitEntity entity) {
        if (entity == null) return null;
        return new Habit(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                HabitType.valueOf(entity.getType()),
                entity.getDescription(),
                entity.getActive(),
                entity.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()
        );
    }

    public HabitEntity toEntity(Habit habit, Long userId) {
        if (habit == null) return null;
        return HabitEntity.builder()
                .id(habit.getId())
                .userId(userId)
                .name(habit.getName())
                .type(habit.getType().name())
                .description(habit.getDescription())
                .active(habit.isActive())
                .createdAt(LocalDateTime.ofInstant(habit.getCreatedAt(), ZoneId.systemDefault()))
                .build();
    }
}
