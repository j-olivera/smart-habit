package com.smart.smart_backend.application.dto.habit.log;

import com.smart.smart_backend.domain.enums.MoodLevel;
import lombok.Builder;

@Builder
public record MoodLogResponseDto(
    Long id,
    Long habitId,
    Long entryId,
    MoodLevel mood,
    boolean hasObservations,
    String eventDescription,
    boolean socialized,
    String socialWith
) {}
