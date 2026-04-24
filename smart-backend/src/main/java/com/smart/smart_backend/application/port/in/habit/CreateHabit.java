package com.smart.smart_backend.application.port.in.habit;

import com.smart.smart_backend.application.dto.habit.HabitRequestDto;
import com.smart.smart_backend.application.dto.habit.HabitResponseDto;

public interface CreateHabit {
    HabitResponseDto execute(HabitRequestDto request, Long userId);
}
