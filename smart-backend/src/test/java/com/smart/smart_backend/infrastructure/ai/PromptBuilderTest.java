package com.smart.smart_backend.infrastructure.ai;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;
import com.smart.smart_backend.application.dto.habit.log.ExerciseLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.MoodLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.NutritionLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.SleepLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.StudyLogResponseDto;
import com.smart.smart_backend.domain.enums.MoodLevel;
import com.smart.smart_backend.domain.enums.MuscularGroup;
import com.smart.smart_backend.domain.enums.NutritionRating;
import com.smart.smart_backend.domain.enums.SleepQuality;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PromptBuilderTest {

    private PromptBuilder promptBuilder;

    @BeforeEach
    void setUp() {
        promptBuilder = new PromptBuilder();
    }

    @Test
    void shouldBuildPromptWithAllSections() {
        // Given
        List<DailyEntryWithLogsResult> entries = List.of(
                DailyEntryWithLogsResult.builder()
                        .id(1L)
                        .userId(1L)
                        .date(LocalDate.of(2026, 4, 21))
                        .studyLog(StudyLogResponseDto.builder()
                                .id(1L)
                                .studied(true)
                                .hours(2.0f)
                                .subject("Java")
                                .build())
                        .exerciseLog(ExerciseLogResponseDto.builder()
                                .id(1L)
                                .exercised(true)
                                .hours(1.0f)
                                .muscularGroup(MuscularGroup.CHEST)
                                .energyLevel(80)
                                .build())
                        .nutritionLog(NutritionLogResponseDto.builder()
                                .id(1L)
                                .rating(NutritionRating.GOOD)
                                .metGoal(true)
                                .build())
                        .moodLog(MoodLogResponseDto.builder()
                                .id(1L)
                                .mood(MoodLevel.HAPPY)
                                .build())
                        .sleepLog(SleepLogResponseDto.builder()
                                .id(1L)
                                .hours(7.5f)
                                .quality(SleepQuality.GOOD)
                                .napped(false)
                                .build())
                        .personalLogs(List.of())
                        .build()
        );

        LocalDate weekStart = LocalDate.of(2026, 4, 20);
        LocalDate weekEnd = LocalDate.of(2026, 4, 26);

        // When
        String prompt = promptBuilder.build(entries, weekStart, weekEnd);

        // Then
        assertNotNull(prompt);
        assertTrue(prompt.contains("Semana del 2026-04-20 al 2026-04-26"));
        assertTrue(prompt.contains("--- 2026-04-21 ---"));
        assertTrue(prompt.contains("Estudio: Sí — 2.0hs de Java"));
        assertTrue(prompt.contains("Ejercicio: Sí — 1.0hs, CHEST, energía 80%"));
        assertTrue(prompt.contains("Alimentación: GOOD — cumplió objetivo"));
        assertTrue(prompt.contains("Ánimo: HAPPY"));
        assertTrue(prompt.contains("Sueño: 7.5hs (GOOD)"));
        assertTrue(prompt.contains("## RESUMEN GENERAL"));
        assertTrue(prompt.contains("## FORTALEZAS"));
        assertTrue(prompt.contains("## ÁREAS DE MEJORA"));
        assertTrue(prompt.contains("## RIESGOS DETECTADOS"));
        assertTrue(prompt.contains("## RECOMENDACIONES"));
    }

    @Test
    void shouldHandleSkippedHabits() {
        // Given
        List<DailyEntryWithLogsResult> entries = List.of(
                DailyEntryWithLogsResult.builder()
                        .id(1L)
                        .userId(1L)
                        .date(LocalDate.of(2026, 4, 21))
                        .studyLog(StudyLogResponseDto.builder()
                                .id(1L)
                                .studied(false)
                                .skipReason("cansado")
                                .build())
                        .exerciseLog(ExerciseLogResponseDto.builder()
                                .id(1L)
                                .exercised(false)
                                .skipReason("dolor muscular")
                                .build())
                        .nutritionLog(null)
                        .moodLog(null)
                        .sleepLog(null)
                        .personalLogs(List.of())
                        .build()
        );

        LocalDate weekStart = LocalDate.of(2026, 4, 20);
        LocalDate weekEnd = LocalDate.of(2026, 4, 26);

        // When
        String prompt = promptBuilder.build(entries, weekStart, weekEnd);

        // Then
        assertTrue(prompt.contains("Estudio: No — cansado"));
        assertTrue(prompt.contains("Ejercicio: No — dolor muscular"));
    }

    @Test
    void shouldHandleEmptyLogs() {
        // Given
        List<DailyEntryWithLogsResult> entries = List.of(
                DailyEntryWithLogsResult.builder()
                        .id(1L)
                        .userId(1L)
                        .date(LocalDate.of(2026, 4, 21))
                        .studyLog(null)
                        .exerciseLog(null)
                        .nutritionLog(null)
                        .moodLog(null)
                        .sleepLog(null)
                        .personalLogs(List.of())
                        .build()
        );

        LocalDate weekStart = LocalDate.of(2026, 4, 20);
        LocalDate weekEnd = LocalDate.of(2026, 4, 26);

        // When
        String prompt = promptBuilder.build(entries, weekStart, weekEnd);

        // Then
        assertNotNull(prompt);
        assertTrue(prompt.contains("--- 2026-04-21 ---"));
    }

    @Test
    void shouldHandleNapData() {
        // Given
        List<DailyEntryWithLogsResult> entries = List.of(
                DailyEntryWithLogsResult.builder()
                        .id(1L)
                        .userId(1L)
                        .date(LocalDate.of(2026, 4, 21))
                        .sleepLog(SleepLogResponseDto.builder()
                                .id(1L)
                                .hours(6.0f)
                                .quality(SleepQuality.REGULAR)
                                .napped(true)
                                .napHours(1.0f)
                                .build())
                        .personalLogs(List.of())
                        .build()
        );

        LocalDate weekStart = LocalDate.of(2026, 4, 20);
        LocalDate weekEnd = LocalDate.of(2026, 4, 26);

        // When
        String prompt = promptBuilder.build(entries, weekStart, weekEnd);

        // Then
        assertTrue(prompt.contains("Sueño: 6.0hs (REGULAR) + siesta 1.0hs"));
    }

    @Test
    void shouldHandleNullStudyLogs() {
        // Given - null logs means no output for that category
        List<DailyEntryWithLogsResult> entries = List.of(
                DailyEntryWithLogsResult.builder()
                        .id(1L)
                        .userId(1L)
                        .date(LocalDate.of(2026, 4, 21))
                        .studyLog(null)
                        .exerciseLog(null)
                        .nutritionLog(null)
                        .moodLog(null)
                        .sleepLog(null)
                        .personalLogs(List.of())
                        .build()
        );

        LocalDate weekStart = LocalDate.of(2026, 4, 20);
        LocalDate weekEnd = LocalDate.of(2026, 4, 26);

        // When
        String prompt = promptBuilder.build(entries, weekStart, weekEnd);

        // Then
        assertNotNull(prompt);
        assertTrue(prompt.contains("--- 2026-04-21 ---"));
        assertFalse(prompt.contains("Estudio:")); // No output when null
    }

    @Test
    void shouldHandleNullExerciseLogs() {
        // Given
        List<DailyEntryWithLogsResult> entries = List.of(
                DailyEntryWithLogsResult.builder()
                        .id(1L)
                        .userId(1L)
                        .date(LocalDate.of(2026, 4, 21))
                        .studyLog(null)
                        .exerciseLog(null)
                        .nutritionLog(null)
                        .moodLog(null)
                        .sleepLog(null)
                        .personalLogs(List.of())
                        .build()
        );

        LocalDate weekStart = LocalDate.of(2026, 4, 20);
        LocalDate weekEnd = LocalDate.of(2026, 4, 26);

        // When
        String prompt = promptBuilder.build(entries, weekStart, weekEnd);

        // Then
        assertNotNull(prompt);
        assertTrue(prompt.contains("--- 2026-04-21 ---"));
        assertFalse(prompt.contains("Ejercicio:")); // No output when null
    }

    @Test
    void shouldHandleMultiplePersonalLogs() {
        // Given
        List<DailyEntryWithLogsResult> entries = List.of(
                DailyEntryWithLogsResult.builder()
                        .id(1L)
                        .userId(1L)
                        .date(LocalDate.of(2026, 4, 21))
                        .personalLogs(List.of(
                                com.smart.smart_backend.application.dto.habit.log.PersonalLogResponseDto.builder()
                                        .id(1L)
                                        .habitId(10L)
                                        .completed(true)
                                        .hours(1.0f)
                                        .build(),
                                com.smart.smart_backend.application.dto.habit.log.PersonalLogResponseDto.builder()
                                        .id(2L)
                                        .habitId(11L)
                                        .completed(false)
                                        .description("mañana sí")
                                        .build()
                        ))
                        .build()
        );

        LocalDate weekStart = LocalDate.of(2026, 4, 20);
        LocalDate weekEnd = LocalDate.of(2026, 4, 26);

        // When
        String prompt = promptBuilder.build(entries, weekStart, weekEnd);

        // Then
        assertNotNull(prompt);
        assertTrue(prompt.contains("Hábito Personal (ID 10): Completado — 1.0hs"));
        assertTrue(prompt.contains("Hábito Personal (ID 11): Pendiente (mañana sí)"));
    }

    @Test
    void shouldFormatWeekRangeCorrectly() {
        // Given
        List<DailyEntryWithLogsResult> entries = List.of(
                DailyEntryWithLogsResult.builder()
                        .id(1L)
                        .userId(1L)
                        .date(LocalDate.of(2026, 4, 21))
                        .studyLog(StudyLogResponseDto.builder().id(1L).studied(true).hours(2.0f).subject("Java").build())
                        .personalLogs(List.of())
                        .build()
        );

        LocalDate weekStart = LocalDate.of(2026, 1, 5); // Enero, cambia de mes
        LocalDate weekEnd = LocalDate.of(2026, 1, 11);

        // When
        String prompt = promptBuilder.build(entries, weekStart, weekEnd);

        // Then
        assertNotNull(prompt);
        assertTrue(prompt.contains("Semana del 2026-01-05 al 2026-01-11"));
    }
}