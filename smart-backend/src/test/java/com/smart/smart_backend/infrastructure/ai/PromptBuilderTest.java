package com.smart.smart_backend.infrastructure.ai;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;
import com.smart.smart_backend.application.dto.habit.log.*;
import com.smart.smart_backend.domain.enums.MoodLevel;
import com.smart.smart_backend.domain.enums.MuscularGroup;
import com.smart.smart_backend.domain.enums.NutritionRating;
import com.smart.smart_backend.domain.enums.SleepQuality;
import com.smart.smart_backend.infrastructure.repository.habit.JpaHabitRepository;
import com.smart.smart_backend.infrastructure.model.habit.HabitEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromptBuilderTest {

        @Mock
        private JpaHabitRepository habitRepositoryJpa;

        @InjectMocks
        private PromptBuilder promptBuilder;

        @Test
        void shouldBuildPromptWithAllNewSectionsAndTranslations() {
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
                                                                .hasObservation(true)
                                                                .build())
                                                .moodLog(MoodLogResponseDto.builder()
                                                                .id(1L)
                                                                .mood(MoodLevel.HAPPY)
                                                                .eventDescription("Gran día")
                                                                .socialized(true)
                                                                .socialWith("Familia")
                                                                .build())
                                                .sleepLog(SleepLogResponseDto.builder()
                                                                .id(1L)
                                                                .hours(7.5f)
                                                                .quality(SleepQuality.GOOD)
                                                                .napped(true)
                                                                .napHours(0.5f)
                                                                .build())
                                                .personalLogs(List.of(
                                                                PersonalLogResponseDto.builder()
                                                                                .habitId(10L)
                                                                                .completed(true)
                                                                                .description("Meditación matutina")
                                                                                .build()))
                                                .build());

                // Mock para el nombre del hábito personal
                HabitEntity mockHabit = new HabitEntity();
                mockHabit.setName("Meditación");
                when(habitRepositoryJpa.findById(10L)).thenReturn(Optional.of(mockHabit));

                LocalDate weekStart = LocalDate.of(2026, 4, 20);
                LocalDate weekEnd = LocalDate.of(2026, 4, 26);

                // When
                String prompt = promptBuilder.build(entries, weekStart, weekEnd);

                // Then
                assertNotNull(prompt);
                assertTrue(prompt.contains("Semana del 2026-04-20 al 2026-04-26"));
                assertTrue(prompt.contains("Estudio: Sí — 2.0hs de Java"));
                assertTrue(prompt.contains("Ejercicio: Sí — 1.0hs, CHEST, energía 80%"));
                assertTrue(prompt.contains("Alimentación: Buena — cumplió su objetivo nutricional"));
                assertTrue(prompt.contains("Ánimo: Feliz — motivo: \"Gran día\" | Socializó con Familia"));
                assertTrue(prompt.contains("Sueño: 7.5hs (Buena) + siesta 0.5hs"));
                assertTrue(prompt.contains("Meditación: Completado (Meditación matutina)"));

                // Verificar secciones del nuevo System Prompt
                assertTrue(prompt.contains("## RESUMEN DE LA SEMANA"));
                assertTrue(prompt.contains("## LO QUE FUNCIONÓ"));
                assertTrue(prompt.contains("## DÓNDE HUBO DIFICULTADES"));
                assertTrue(prompt.contains("## PATRONES A PRESTAR ATENCIÓN"));
                assertTrue(prompt.contains("## PARA LA PRÓXIMA SEMANA"));
                assertTrue(prompt.contains("## UN PENSAMIENTO FINAL"));
        }

        @Test
        void shouldHandleSkippedHabitsWithNewSpanishFormat() {
                // Given
                List<DailyEntryWithLogsResult> entries = List.of(
                                DailyEntryWithLogsResult.builder()
                                                .date(LocalDate.of(2026, 4, 21))
                                                .studyLog(StudyLogResponseDto.builder()
                                                                .studied(false)
                                                                .skipReason("Cansancio")
                                                                .build())
                                                .exerciseLog(ExerciseLogResponseDto.builder()
                                                                .exercised(false)
                                                                .skipReason("Lesión")
                                                                .build())
                                                .personalLogs(List.of())
                                                .build());

                // When
                String prompt = promptBuilder.build(entries, LocalDate.now(), LocalDate.now());

                // Then
                assertTrue(prompt.contains("Estudio: No — Cansancio"));
                assertTrue(prompt.contains("Ejercicio: No — Lesión"));
        }

        @Test
        void shouldHandleSocialStatusCorrecty() {
                // Given
                List<DailyEntryWithLogsResult> entries = List.of(
                                DailyEntryWithLogsResult.builder()
                                                .date(LocalDate.of(2026, 4, 21))
                                                .moodLog(MoodLogResponseDto.builder()
                                                                .mood(MoodLevel.NEUTRAL)
                                                                .socialized(false)
                                                                .build())
                                                .build());

                // When
                String prompt = promptBuilder.build(entries, LocalDate.now(), LocalDate.now());

                // Then
                assertTrue(prompt.contains("Ánimo: Neutro | No socializó"));
        }
}