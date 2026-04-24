package com.smart.smart_backend.application.port.in.registers;

import com.smart.smart_backend.application.dto.habit.DailyEntryResponseDto;

import java.time.LocalDate;
import java.util.Optional;

public interface GetDailyEntryReport {
    Optional<?> execute(Long userId, LocalDate date);
}
