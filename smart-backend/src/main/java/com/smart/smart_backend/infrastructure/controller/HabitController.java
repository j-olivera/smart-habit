package com.smart.smart_backend.infrastructure.controller;

import com.smart.smart_backend.application.dto.habit.HabitRequestDto;
import com.smart.smart_backend.application.dto.habit.HabitResponseDto;
import com.smart.smart_backend.application.port.in.habit.CreateHabit;
import com.smart.smart_backend.application.port.in.habit.DesactivateHabit;
import com.smart.smart_backend.application.port.in.habit.GetUserHabits;
import com.smart.smart_backend.domain.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {

    private final CreateHabit createHabitUseCase;
    private final GetUserHabits getUserHabitsUseCase;
    private final DesactivateHabit desactivateHabitUseCase;

    @PostMapping
    public ResponseEntity<HabitResponseDto> createHabit(
            @AuthenticationPrincipal User user,
            @RequestBody HabitRequestDto requestDto) {
        HabitResponseDto response = createHabitUseCase.execute(requestDto, user.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HabitResponseDto>> getHabits(@AuthenticationPrincipal User user) {
        List<HabitResponseDto> response = getUserHabitsUseCase.execute(user.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivateHabit(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        desactivateHabitUseCase.execute(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
