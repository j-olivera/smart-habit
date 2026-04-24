package com.smart.smart_backend.application.dto.habit.log;

import com.smart.smart_backend.domain.enums.SleepQuality;
import lombok.Builder;

@Builder
public record SleepLogResponseDto(
    Long id,
    Long habitId,
    Long entryId,
    Float hours,
    SleepQuality quality,
    boolean napped,
    Float napHours,
    boolean napNeeded
) {}
