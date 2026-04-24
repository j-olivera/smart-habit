package com.smart.smart_backend.application.port.in.registers;

import java.time.LocalDate;
import java.util.List;

public interface GetWeeklyReport {
    List<?> execute(Long userId, LocalDate date);
}
