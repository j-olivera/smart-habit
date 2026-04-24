package com.smart.smart_backend.application.mapper.habit;

import com.smart.smart_backend.application.dto.habit.HabitRequestDto;
import com.smart.smart_backend.application.dto.habit.HabitResponseDto;
import com.smart.smart_backend.domain.model.habit.Habit;

public class HabitMapper {
    public static HabitResponseDto toResponse(Habit habit){
        return new HabitResponseDto(
                habit.getId(),
                habit.getUserId(),
                habit.getName(),
                habit.getType(),
                habit.getDescription(),
                habit.isActive(),
                habit.getCreatedAt()
        );
    }
    public static Habit toDomain(HabitRequestDto habitRequestDto){
        return Habit.create(
                null,
                habitRequestDto.name(),
                habitRequestDto.type(),
                habitRequestDto.description(),
                true
        );
    }
}
