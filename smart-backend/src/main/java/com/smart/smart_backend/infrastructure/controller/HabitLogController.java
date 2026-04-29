package com.smart.smart_backend.infrastructure.controller;

import com.smart.smart_backend.application.dto.habit.log.*;
import com.smart.smart_backend.application.port.in.logs.*;
import com.smart.smart_backend.domain.model.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class HabitLogController {

    private final RegisterStudyLog registerStudyLogUseCase;
    private final RegisterExerciseLog registerExerciseLogUseCase;
    private final RegisterMoodLog registerMoodLogUseCase;
    private final RegisterNutritionLog registerNutritionLogUseCase;
    private final RegisterSleepLog registerSleepLogUseCase;
    private final RegisterPersonalLog registerPersonalLogUseCase;

    @PostMapping("/study")
    public ResponseEntity<StudyLogResponseDto> registerStudy(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody StudyLogRequestDto requestDto) {
        return ResponseEntity.ok(registerStudyLogUseCase.execute(user.getId(), requestDto));
    }

    @PostMapping("/exercise")
    public ResponseEntity<ExerciseLogResponseDto> registerExercise(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ExerciseLogRequestDto requestDto) {
        return ResponseEntity.ok(registerExerciseLogUseCase.execute(user.getId(), requestDto));
    }

    @PostMapping("/mood")
    public ResponseEntity<MoodLogResponseDto> registerMood(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody MoodLogRequestDto requestDto) {
        return ResponseEntity.ok(registerMoodLogUseCase.execute(user.getId(), requestDto));
    }

    @PostMapping("/nutrition")
    public ResponseEntity<NutritionLogResponseDto> registerNutrition(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody NutritionLogRequestDto requestDto) {
        return ResponseEntity.ok(registerNutritionLogUseCase.execute(user.getId(), requestDto));
    }

    @PostMapping("/sleep")
    public ResponseEntity<SleepLogResponseDto> registerSleep(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SleepLogRequestDto requestDto) {
        return ResponseEntity.ok(registerSleepLogUseCase.execute(user.getId(), requestDto));
    }

    @PostMapping("/personal")
    public ResponseEntity<PersonalLogResponseDto> registerPersonal(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PersonalLogRequestDto requestDto) {
        return ResponseEntity.ok(registerPersonalLogUseCase.execute(user.getId(), requestDto));
    }
}
