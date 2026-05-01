package com.smart.smart_backend.application.usecase.logs;

import com.smart.smart_backend.application.dto.habit.log.ExerciseLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.ExerciseLogResponseDto;
import com.smart.smart_backend.application.port.out.logs.ExerciseLogRepositoryPort;
import com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort;
import com.smart.smart_backend.domain.enums.MuscularGroup;
import com.smart.smart_backend.domain.exception.EntryNotFoundException;
import com.smart.smart_backend.domain.model.habit.DailyEntry;
import com.smart.smart_backend.domain.model.habit.ExerciseLog;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogUseCasesAdditionalTest {

        @Mock
        private DailyEntryRepositoryPort dailyEntryRepositoryPort;
        @Mock
        private ExerciseLogRepositoryPort exerciseLogRepositoryPort;

        private RegisterExerciseLogUseCase registerExerciseLogUseCase;

        @BeforeEach
        void setUp() {
                registerExerciseLogUseCase = new RegisterExerciseLogUseCase(
                                dailyEntryRepositoryPort, exerciseLogRepositoryPort);
        }

        // ──────────────────────────────────────────────
        // ExerciseLog Tests
        // ──────────────────────────────────────────────

        @Test
        void shouldRegisterExerciseLogSuccessfully() {
                // Given
                Long userId = 1L;
                Long entryId = 20L;

                DailyEntry entry = DailyEntry.builder()
                                .id(entryId)
                                .userId(userId)
                                .date(LocalDate.now())
                                .createdAt(Instant.now())
                                .build();

                ExerciseLogRequestDto request = new ExerciseLogRequestDto(
                                entryId, true, 1.5f, MuscularGroup.CHEST, 80, null);

                when(dailyEntryRepositoryPort.findByIdAndUserId(entryId, userId)).thenReturn(Optional.of(entry));
                when(exerciseLogRepositoryPort.findByEntryId(entryId)).thenReturn(Optional.empty());
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
        void shouldThrowEntryNotFoundException() {
                // Given
                Long userId = 1L;
                Long entryId = 999L;

                ExerciseLogRequestDto request = new ExerciseLogRequestDto(
                                entryId, true, 1.0f, MuscularGroup.CHEST, 80, null);

                when(dailyEntryRepositoryPort.findByIdAndUserId(entryId, userId)).thenReturn(Optional.empty());

                // When/Then
                assertThatThrownBy(() -> registerExerciseLogUseCase.execute(userId, request))
                                .isInstanceOf(EntryNotFoundException.class);
        }

        @Test
        void shouldSkipExerciseWhenNotExercised() {
                // Given
                Long userId = 1L;
                Long entryId = 20L;

                DailyEntry entry = DailyEntry.builder()
                                .id(entryId).userId(userId).date(LocalDate.now()).build();

                // exercised = false, skipReason provided
                ExerciseLogRequestDto request = new ExerciseLogRequestDto(
                                entryId, false, null, null, null, "cansado");

                when(dailyEntryRepositoryPort.findByIdAndUserId(entryId, userId)).thenReturn(Optional.of(entry));
                when(exerciseLogRepositoryPort.findByEntryId(entryId)).thenReturn(Optional.empty());
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
                Long entryId = 20L;
                Long existingLogId = 500L;

                DailyEntry entry = DailyEntry.builder()
                                .id(entryId).userId(userId).date(LocalDate.now()).build();

                // Use the constructor directly
                ExerciseLog existingLog = new ExerciseLog(existingLogId, entryId, true, 1.0f, MuscularGroup.CHEST, 80,
                                null);

                ExerciseLogRequestDto request = new ExerciseLogRequestDto(
                                entryId, true, 2.0f, MuscularGroup.LEGS, 90, null);

                when(dailyEntryRepositoryPort.findByIdAndUserId(entryId, userId)).thenReturn(Optional.of(entry));
                when(exerciseLogRepositoryPort.findByEntryId(entryId)).thenReturn(Optional.of(existingLog));
                when(exerciseLogRepositoryPort.save(any(ExerciseLog.class))).thenAnswer(inv -> inv.getArgument(0));

                // When
                ExerciseLogResponseDto result = registerExerciseLogUseCase.execute(userId, request);

                // Then
                assertThat(result.hours()).isEqualTo(2.0f);
                assertThat(result.muscularGroup()).isEqualTo(MuscularGroup.LEGS);
        }
}