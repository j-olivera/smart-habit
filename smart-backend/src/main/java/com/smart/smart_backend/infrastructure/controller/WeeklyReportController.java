package com.smart.smart_backend.infrastructure.controller;

import com.smart.smart_backend.application.dto.report.WeeklyReportResponse;
import com.smart.smart_backend.application.dto.report.WeeklyReportSummary;
import com.smart.smart_backend.application.port.in.report.GenerateWeeklyReportPort;
import com.smart.smart_backend.application.port.in.report.GetWeeklyReportByIdPort;
import com.smart.smart_backend.application.port.in.report.GetWeeklyReportsPort;
import com.smart.smart_backend.domain.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class WeeklyReportController {

    private final GenerateWeeklyReportPort generateWeeklyReportUseCase;
    private final GetWeeklyReportsPort getWeeklyReportsUseCase;
    private final GetWeeklyReportByIdPort getWeeklyReportByIdUseCase;

    @GetMapping
    public ResponseEntity<List<WeeklyReportSummary>> getWeeklyReports(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(getWeeklyReportsUseCase.execute(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WeeklyReportResponse> getWeeklyReportById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return getWeeklyReportByIdUseCase.execute(user.getId(), id)
                .map(result -> ResponseEntity.ok(WeeklyReportResponse.from(result)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/weekly/generate")
    public ResponseEntity<WeeklyReportResponse> generateWeeklyReport(
            @AuthenticationPrincipal User user) {
        LocalDate weekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        var result = generateWeeklyReportUseCase.execute(
                new com.smart.smart_backend.application.port.in.report.GenerateWeeklyReportCommand(
                        user.getId(),
                        weekStart,
                        "USER"
                )
        );
        return new ResponseEntity<>(WeeklyReportResponse.from(result), HttpStatus.OK);
    }
}