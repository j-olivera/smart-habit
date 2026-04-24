package com.smart.smart_backend.application.dto.habit.log;

import com.smart.smart_backend.domain.enums.MuscularGroup;
import lombok.Builder;

@Builder
public record ExerciseLogResponseDto(
    Long id,
    Long habitId,
    Long entryId,
    boolean exercised,
    Float hours,
    MuscularGroup muscularGroup,
    Integer energyLevel,
    String skipReason
) {}
