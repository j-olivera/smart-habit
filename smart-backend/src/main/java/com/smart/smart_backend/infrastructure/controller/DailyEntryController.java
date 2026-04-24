package com.smart.smart_backend.infrastructure.controller;

import com.smart.smart_backend.application.dto.habit.DailyEntryResponseDto;
import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;
import com.smart.smart_backend.application.dto.habit.WeeklyEntriesReportDto;
import com.smart.smart_backend.application.port.in.habit.GetDailyEntryUseCase;
import com.smart.smart_backend.application.port.in.habit.GetWeeklyEntriesUseCase;
import com.smart.smart_backend.application.port.in.registers.CreateDailyEntry;
import com.smart.smart_backend.domain.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/daily-entries")
@RequiredArgsConstructor
public class DailyEntryController {

    private final CreateDailyEntry createDailyEntryUseCase;
    private final GetDailyEntryUseCase getDailyEntryUseCase;
    private final GetWeeklyEntriesUseCase getWeeklyEntriesUseCase;

    @PostMapping
    public ResponseEntity<DailyEntryResponseDto> createEntry(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate entryDate = (date != null) ? date : LocalDate.now();
        DailyEntryResponseDto response = createDailyEntryUseCase.execute(user.getId(), entryDate);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{date}")
    public ResponseEntity<DailyEntryWithLogsResult> getEntryByDate(
            @AuthenticationPrincipal User user,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyEntryWithLogsResult response = getDailyEntryUseCase.execute(user.getId(), date);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/weekly")
    public ResponseEntity<WeeklyEntriesReportDto> getWeeklyReport(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        WeeklyEntriesReportDto response = getWeeklyEntriesUseCase.execute(user.getId(), weekStart);
        return ResponseEntity.ok(response);
    }
}
