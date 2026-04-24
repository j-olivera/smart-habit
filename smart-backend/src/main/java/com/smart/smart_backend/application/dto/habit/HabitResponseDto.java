package com.smart.smart_backend.application.dto.habit;

import com.smart.smart_backend.domain.enums.HabitType;
import java.time.Instant;
import lombok.Builder;

@Builder
public record HabitResponseDto(
    Long id,
    Long userId,
    String name,
    HabitType type,
    String description,
    boolean active,
    Instant createdAt
) {}
