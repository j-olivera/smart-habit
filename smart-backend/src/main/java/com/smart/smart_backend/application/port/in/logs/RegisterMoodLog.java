package com.smart.smart_backend.application.port.in.logs;

import com.smart.smart_backend.application.dto.habit.log.MoodLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.MoodLogResponseDto;

public interface RegisterMoodLog {
    MoodLogResponseDto execute(Long userId, MoodLogRequestDto requestDto);
}
