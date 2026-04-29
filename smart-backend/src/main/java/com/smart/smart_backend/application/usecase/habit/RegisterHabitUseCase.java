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
        if (habit.getType() != com.smart.smart_backend.domain.enums.HabitType.PERSONAL) {
            throw new IllegalArgumentException("Sólo se pueden crear hábitos de tipo PERSONAL");
        }
        Habit save = repositoryPort.saveHabit(habit,userId);

        return HabitMapper.toResponse(save);
    }
}
