package com.smart.smart_backend.application.port.in.logs;

import com.smart.smart_backend.application.dto.habit.log.StudyLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.StudyLogResponseDto;

public interface RegisterStudyLog {
    StudyLogResponseDto execute(Long userId, StudyLogRequestDto requestDto);
}
