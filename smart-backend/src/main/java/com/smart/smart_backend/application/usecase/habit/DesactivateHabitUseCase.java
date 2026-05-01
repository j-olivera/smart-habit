package com.smart.smart_backend.application.usecase.habit;

import com.smart.smart_backend.application.port.in.habit.DesactivateHabit;
import com.smart.smart_backend.application.port.out.habit.HabitRepositoryPort;

public class DesactivateHabitUseCase implements DesactivateHabit {
    private final HabitRepositoryPort repositoryPort;

    public DesactivateHabitUseCase(HabitRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public void execute(Long habitId, Long userId) {
        repositoryPort.desactivateHabit(habitId, userId);
    }
}
