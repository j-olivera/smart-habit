package com.smart.smart_backend.infrastructure.controller;

import com.smart.smart_backend.application.dto.report.WeeklyReportResponse;
import com.smart.smart_backend.application.port.in.report.GenerateWeeklyReportPort;
import com.smart.smart_backend.domain.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class WeeklyReportController {

    private final GenerateWeeklyReportPort generateWeeklyReportUseCase;

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