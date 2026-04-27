package com.smart.smart_backend.application.usecase.logs;

import com.smart.smart_backend.application.dto.habit.log.ExerciseLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.ExerciseLogResponseDto;
import com.smart.smart_backend.application.port.out.habit.HabitRepositoryPort;
import com.smart.smart_backend.application.port.out.logs.ExerciseLogRepositoryPort;
import com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort;
import com.smart.smart_backend.domain.enums.HabitType;
import com.smart.smart_backend.domain.enums.MuscularGroup;
import com.smart.smart_backend.domain.exception.EntryNotFoundException;
import com.smart.smart_backend.domain.exception.HabitNotFoundException;
import com.smart.smart_backend.domain.exception.HabitTypeMisMatchException;
import com.smart.smart_backend.domain.model.habit.DailyEntry;
import com.smart.smart_backend.domain.model.habit.ExerciseLog;
import com.smart.smart_backend.domain.model.habit.Habit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogUseCasesAdditionalTest {

    @Mock private HabitRepositoryPort habitRepositoryPort;
    @Mock private DailyEntryRepositoryPort dailyEntryRepositoryPort;
    @Mock private ExerciseLogRepositoryPort exerciseLogRepositoryPort;

    private RegisterExerciseLogUseCase registerExerciseLogUseCase;

    @BeforeEach
    void setUp() {
        registerExerciseLogUseCase = new RegisterExerciseLogUseCase(
                habitRepositoryPort, dailyEntryRepositoryPort, exerciseLogRepositoryPort);
    }

    // ──────────────────────────────────────────────
    // ExerciseLog Tests
    // ──────────────────────────────────────────────

    @Test
    void shouldRegisterExerciseLogSuccessfully() {
        // Given
        Long userId = 1L;
        Long habitId = 10L;
        Long entryId = 20L;

        Habit habit = new Habit(habitId, userId, "Ejercicio", HabitType.EXERCISE, "desc", true, Instant.now());
        
        // Use builder pattern - need correct imports
        DailyEntry entry = DailyEntry.builder()
                .id(entryId)
                .userId(userId)
                .date(LocalDate.now())
                .createdAt(Instant.now())
                .build();

        ExerciseLogRequestDto request = new ExerciseLogRequestDto(
                habitId, entryId, true, 1.5f, MuscularGroup.CHEST, 80, null);

        when(habitRepositoryPort.findByIdAndUserId(habitId, userId)).thenReturn(Optional.of(habit));
        when(dailyEntryRepositoryPort.findByIdAndUserId(entryId, userId)).thenReturn(Optional.of(entry));
        when(exerciseLogRepositoryPort.findByHabitIdAndEntryId(habitId, entryId)).thenReturn(Optional.empty());
        when(exerciseLogRepositoryPort.save(any(ExerciseLog.class))).thenAnswer(inv -> {
            ExerciseLog log = inv.getArgument(0);
            return log;
        });

        // When
        ExerciseLogResponseDto result = registerExerciseLogUseCase.execute(userId, request);

        // Then
        assertThat(result.exercised()).isTrue();
        assertThat(result.hours()).isEqualTo(1.5f);
        assertThat(result.muscularGroup()).isEqualTo(MuscularGroup.CHEST);
    }

    @Test
    void shouldThrowHabitNotFoundException() {
        // Given
        Long userId = 1L;
        Long habitId = 999L;
        Long entryId = 20L;

        ExerciseLogRequestDto request = new ExerciseLogRequestDto(
                habitId, entryId, true, 1.0f, MuscularGroup.CHEST, 80, null);

        when(habitRepositoryPort.findByIdAndUserId(habitId, userId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> registerExerciseLogUseCase.execute(userId, request))
                .isInstanceOf(HabitNotFoundException.class);
    }

    @Test
    void shouldThrowHabitTypeMisMatchException() {
        // Given
        Long userId = 1L;
        Long habitId = 10L;
        Long entryId = 20L;

        // Wrong type - trying to log exercise on study habit
        Habit habit = new Habit(habitId, userId, "Estudiar", HabitType.STUDY, "desc", true, Instant.now());
        ExerciseLogRequestDto request = new ExerciseLogRequestDto(
                habitId, entryId, true, 1.0f, MuscularGroup.CHEST, 80, null);

        when(habitRepositoryPort.findByIdAndUserId(habitId, userId)).thenReturn(Optional.of(habit));

        // When/Then
        assertThatThrownBy(() -> registerExerciseLogUseCase.execute(userId, request))
                .isInstanceOf(HabitTypeMisMatchException.class);
    }

    @Test
    void shouldThrowEntryNotFoundException() {
        // Given
        Long userId = 1L;
        Long habitId = 10L;
        Long entryId = 999L;

        Habit habit = new Habit(habitId, userId, "Ejercicio", HabitType.EXERCISE, "desc", true, Instant.now());
        ExerciseLogRequestDto request = new ExerciseLogRequestDto(
                habitId, entryId, true, 1.0f, MuscularGroup.CHEST, 80, null);

        when(habitRepositoryPort.findByIdAndUserId(habitId, userId)).thenReturn(Optional.of(habit));
        when(dailyEntryRepositoryPort.findByIdAndUserId(entryId, userId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> registerExerciseLogUseCase.execute(userId, request))
                .isInstanceOf(EntryNotFoundException.class);
    }

    @Test
    void shouldSkipExerciseWhenNotExercised() {
        // Given
        Long userId = 1L;
        Long habitId = 10L;
        Long entryId = 20L;

        Habit habit = new Habit(habitId, userId, "Ejercicio", HabitType.EXERCISE, "desc", true, Instant.now());
        DailyEntry entry = DailyEntry.builder()
                .id(entryId).userId(userId).date(LocalDate.now()).build();

        // exercised = false, skipReason provided
        ExerciseLogRequestDto request = new ExerciseLogRequestDto(
                habitId, entryId, false, null, null, null, "cansado");

        when(habitRepositoryPort.findByIdAndUserId(habitId, userId)).thenReturn(Optional.of(habit));
        when(dailyEntryRepositoryPort.findByIdAndUserId(entryId, userId)).thenReturn(Optional.of(entry));
        when(exerciseLogRepositoryPort.findByHabitIdAndEntryId(habitId, entryId)).thenReturn(Optional.empty());
        when(exerciseLogRepositoryPort.save(any(ExerciseLog.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        ExerciseLogResponseDto result = registerExerciseLogUseCase.execute(userId, request);

        // Then
        assertThat(result.exercised()).isFalse();
        assertThat(result.skipReason()).isEqualTo("cansado");
    }

    @Test
    void shouldUpdateExistingExerciseLog() {
        // Given
        Long userId = 1L;
        Long habitId = 10L;
        Long entryId = 20L;
        Long existingLogId = 500L;

        Habit habit = new Habit(habitId, userId, "Ejercicio", HabitType.EXERCISE, "desc", true, Instant.now());
        DailyEntry entry = DailyEntry.builder()
                .id(entryId).userId(userId).date(LocalDate.now()).build();
        
        // Use the constructor directly
        ExerciseLog existingLog = new ExerciseLog(existingLogId, habitId, entryId, true, 1.0f, MuscularGroup.CHEST, 80, null);

        ExerciseLogRequestDto request = new ExerciseLogRequestDto(
                habitId, entryId, true, 2.0f, MuscularGroup.LEGS, 90, null);

        when(habitRepositoryPort.findByIdAndUserId(habitId, userId)).thenReturn(Optional.of(habit));
        when(dailyEntryRepositoryPort.findByIdAndUserId(entryId, userId)).thenReturn(Optional.of(entry));
        when(exerciseLogRepositoryPort.findByHabitIdAndEntryId(habitId, entryId)).thenReturn(Optional.of(existingLog));
        when(exerciseLogRepositoryPort.save(any(ExerciseLog.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        ExerciseLogResponseDto result = registerExerciseLogUseCase.execute(userId, request);

        // Then
        assertThat(result.hours()).isEqualTo(2.0f);
        assertThat(result.muscularGroup()).isEqualTo(MuscularGroup.LEGS);
    }
}