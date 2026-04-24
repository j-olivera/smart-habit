package com.smart.smart_backend.application.usecase.habit;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;
import com.smart.smart_backend.application.port.in.habit.GetDailyEntryUseCase;
import com.smart.smart_backend.application.port.out.habit.DailyEntryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

public class GetDailyEntryService implements GetDailyEntryUseCase {

    private final DailyEntryRepositoryPort dailyEntryRepositoryPort;

    public GetDailyEntryService(DailyEntryRepositoryPort dailyEntryRepositoryPort) {
        this.dailyEntryRepositoryPort = dailyEntryRepositoryPort;
    }

    @Override
    public DailyEntryWithLogsResult execute(Long userId, LocalDate date) {
        return dailyEntryRepositoryPort.findByUserIdAndDateWithLogs(userId, date)
                .orElse(null);
    }
}
