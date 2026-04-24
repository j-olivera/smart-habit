package com.smart.smart_backend.application.usecase.habit;

import com.smart.smart_backend.application.dto.habit.HabitResponseDto;
import com.smart.smart_backend.application.mapper.habit.HabitMapper;
import com.smart.smart_backend.application.port.in.habit.GetUserHabits;
import com.smart.smart_backend.application.port.out.habit.HabitRepositoryPort;

import java.util.List;

public class GetUserHabitsUseCase implements GetUserHabits {
    private final HabitRepositoryPort repositoryPort;

    public GetUserHabitsUseCase(HabitRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public List<HabitResponseDto> execute(Long userId) {
        return repositoryPort.findAllByUserId(userId)
                .stream()
                .map(HabitMapper::toResponse)
                .toList();
    }
}
