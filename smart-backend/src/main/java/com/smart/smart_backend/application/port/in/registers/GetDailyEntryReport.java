package com.smart.smart_backend.application.port.in.registers;

import java.time.LocalDate;
import java.util.Optional;

public interface GetDailyEntryReport {
    Optional<?> execute(Long userId, LocalDate date);
}
