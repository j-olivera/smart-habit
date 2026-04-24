package com.smart.smart_backend.application.port.in.logs;

import com.smart.smart_backend.application.dto.habit.log.ExerciseLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.ExerciseLogResponseDto;

public interface RegisterExerciseLog {
    ExerciseLogResponseDto execute(Long userId, ExerciseLogRequestDto requestDto);
}
