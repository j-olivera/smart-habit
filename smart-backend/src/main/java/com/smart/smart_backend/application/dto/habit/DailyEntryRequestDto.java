package com.smart.smart_backend.application.dto.habit;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;

public record DailyEntryRequestDto(
    @NotNull(message = "Date is required")
    LocalDate date
) {}
