package com.smart.smart_backend.application.port.in.logs;

import com.smart.smart_backend.application.dto.habit.log.SleepLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.SleepLogResponseDto;

public interface RegisterSleepLog {
    SleepLogResponseDto execute(Long userId, SleepLogRequestDto requestDto);
}
