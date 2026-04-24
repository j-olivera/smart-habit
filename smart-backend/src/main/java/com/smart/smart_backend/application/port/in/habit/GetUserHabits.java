package com.smart.smart_backend.application.port.in.habit;

import com.smart.smart_backend.application.dto.habit.HabitResponseDto;

import java.util.List;

public interface GetUserHabits {
    List<HabitResponseDto> execute(Long userId); // esto se tiene que cambiar para extraer el jwt del token
}
