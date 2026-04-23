package com.smart.smart_backend.domain.model.habit;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class DailyEntry {
    private Long id;
    private Long userId; //FK
    private LocalDate date; // unica por dia
    private Instant createdAt;
}
