package com.smart.smart_backend.application.dto.habit.log;

import lombok.Builder;

@Builder
public record StudyLogResponseDto(
    Long id,
    Long habitId,
    Long entryId,
    boolean studied,
    Float hours,
    String subject,
    String skipReason
) {}
