package com.smart.smart_backend.application.usecase.habit;

import com.smart.smart_backend.application.dto.habit.HabitRequestDto;
import com.smart.smart_backend.application.dto.habit.HabitResponseDto;
import com.smart.smart_backend.application.mapper.habit.HabitMapper;
import com.smart.smart_backend.application.port.in.habit.CreateHabit;
import com.smart.smart_backend.application.port.out.habit.HabitRepositoryPort;
import com.smart.smart_backend.domain.model.habit.Habit;

public class RegisterHabitUseCase implements CreateHabit {

    private final HabitRepositoryPort repositoryPort;

    public RegisterHabitUseCase(HabitRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public HabitResponseDto execute(HabitRequestDto request, Long userId) {

        Habit habit = HabitMapper.toDomain(request);
        Habit save = repositoryPort.saveHabit(habit,userId);

        return HabitMapper.toResponse(save);
    }
}
