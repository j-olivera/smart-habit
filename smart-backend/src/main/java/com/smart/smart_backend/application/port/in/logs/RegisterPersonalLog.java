package com.smart.smart_backend.application.port.in.logs;

import com.smart.smart_backend.application.dto.habit.log.PersonalLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.PersonalLogResponseDto;

public interface RegisterPersonalLog {
    PersonalLogResponseDto execute(Long userId, PersonalLogRequestDto requestDto);
}
