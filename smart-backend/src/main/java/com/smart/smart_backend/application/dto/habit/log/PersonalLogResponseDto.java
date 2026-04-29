package com.smart.smart_backend.application.dto.habit.log;

import lombok.Builder;

@Builder
public record PersonalLogResponseDto(
    Long id,
    Long habitId,
    Long entryId,
    boolean completed,
    Float hours,
    String description
) {}
