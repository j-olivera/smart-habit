package com.smart.smart_backend.application.usecase.habit;

import com.smart.smart_backend.application.dto.habit.DailyEntryResponseDto;
import com.smart.smart_backend.application.port.in.registers.CreateDailyEntry;
import com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort;
import com.smart.smart_backend.domain.model.habit.DailyEntry;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.Instant;

@RequiredArgsConstructor
public class CreateDailyEntryService implements CreateDailyEntry {

    private final DailyEntryRepositoryPort dailyEntryRepositoryPort;

    @Override
    public DailyEntryResponseDto execute(Long userId, LocalDate date) {
        DailyEntry entry = dailyEntryRepositoryPort.findByUserIdAndDate(userId, date)
                .orElseGet(() -> dailyEntryRepositoryPort.save(
                        DailyEntry.builder()
                                .userId(userId)
                                .date(date)
                                .createdAt(Instant.now())
                                .build()
                ));

        return new DailyEntryResponseDto(
                entry.getId(),
                entry.getUserId(),
                entry.getDate(),
                entry.getCreatedAt()
        );
    }
}
