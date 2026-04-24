package com.smart.smart_backend.application.dto.habit;

import java.time.Instant;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record DailyEntryResponseDto(
    Long id,
    Long userId,
    LocalDate date,
    Instant createdAt
) {}
