package com.smart.smart_backend.application.usecase.report;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;
import com.smart.smart_backend.application.dto.habit.WeeklyEntriesReportDto;
import com.smart.smart_backend.application.dto.habit.log.ExerciseLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.MoodLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.NutritionLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.SleepLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.StudyLogResponseDto;
import com.smart.smart_backend.application.dto.report.WeeklyReportResult;
import com.smart.smart_backend.application.port.in.habit.GetWeeklyEntriesUseCase;
import com.smart.smart_backend.application.port.in.report.GenerateWeeklyReportCommand;
import com.smart.smart_backend.application.port.out.ai.AiAssistantPort;
import com.smart.smart_backend.application.port.out.ai.PromptBuilderPort;
import com.smart.smart_backend.application.port.out.report.WeeklyReportRepositoryPort;
import com.smart.smart_backend.domain.enums.MoodLevel;
import com.smart.smart_backend.domain.enums.MuscularGroup;
import com.smart.smart_backend.domain.enums.NutritionRating;
import com.smart.smart_backend.domain.enums.SleepQuality;
import com.smart.smart_backend.domain.exception.InsufficientDataException;
import com.smart.smart_backend.domain.model.report.WeeklyReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerateWeeklyReportUseCaseTest {

    @Mock
    private GetWeeklyEntriesUseCase getWeeklyEntriesUseCase;

    @Mock
    private AiAssistantPort aiAssistant;

    @Mock
    private WeeklyReportRepositoryPort reportRepo;

    @Mock
    private PromptBuilderPort promptBuilder;

    private GenerateWeeklyReportUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GenerateWeeklyReportUseCase(
                getWeeklyEntriesUseCase,
                aiAssistant,
                reportRepo,
                promptBuilder
        );
    }

    @Test
    void shouldGenerateWeeklyReportSuccessfully() {
        // Given
        Long userId = 1L;
        LocalDate weekStart = LocalDate.of(2026, 4, 20);
        GenerateWeeklyReportCommand command = new GenerateWeeklyReportCommand(userId, weekStart, "USER");

        List<DailyEntryWithLogsResult> entries = List.of(
                DailyEntryWithLogsResult.builder()
                        .id(1L)
                        .userId(userId)
                        .date(LocalDate.of(2026, 4, 21))
                        .studyLogs(List.of(StudyLogResponseDto.builder()
                                .id(1L).studied(true).hours(2.0f).subject("Java").build()))
                        .exerciseLogs(List.of(ExerciseLogResponseDto.builder()
                                .id(1L).exercised(true).hours(1.0f)
                                .muscularGroup(MuscularGroup.CHEST).energyLevel(80).build()))
                        .nutritionLogs(List.of(NutritionLogResponseDto.builder()
                                .id(1L).rating(NutritionRating.GOOD).metGoal(true).build()))
                        .moodLogs(List.of(MoodLogResponseDto.builder()
                                .id(1L).mood(MoodLevel.HAPPY).build()))
                        .sleepLogs(List.of(SleepLogResponseDto.builder()
                                .id(1L).hours(7.5f).quality(SleepQuality.GOOD).napped(false).build()))
                        .build(),
                DailyEntryWithLogsResult.builder()
                        .id(2L)
                        .userId(userId)
                        .date(LocalDate.of(2026, 4, 22))
                        .studyLogs(List.of(StudyLogResponseDto.builder()
                                .id(2L).studied(true).hours(3.0f).subject("Python").build()))
                        .build(),
                DailyEntryWithLogsResult.builder()
                        .id(3L)
                        .userId(userId)
                        .date(LocalDate.of(2026, 4, 23))
                        .studyLogs(List.of(StudyLogResponseDto.builder()
                                .id(3L).studied(false).skipReason("descanso").build()))
                        .build()
        );

        WeeklyEntriesReportDto weeklyReport = WeeklyEntriesReportDto.builder()
                .userId(userId)
                .weekStart(weekStart)
                .weekEnd(weekStart.plusDays(6))
                .dailyEntries(entries)
                .build();

        when(getWeeklyEntriesUseCase.execute(eq(userId), any(LocalDate.class))).thenReturn(weeklyReport);
        when(promptBuilder.build(any(), any(), any())).thenReturn("prompt test");
        when(aiAssistant.generateWeeklyInsight(any())).thenReturn("## RESUMEN GENERAL\nTest report");
        when(reportRepo.findByUserIdAndWeekStart(userId, weekStart)).thenReturn(java.util.Optional.empty());
        when(reportRepo.save(any(WeeklyReport.class))).thenAnswer(inv -> {
            WeeklyReport report = inv.getArgument(0);
            report.setId(1L);
            return report;
        });

        // When
        WeeklyReportResult result = useCase.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(weekStart, result.weekStart());
        assertEquals(weekStart.plusDays(6), result.weekEnd());
        assertNotNull(result.generatedAt());

        verify(getWeeklyEntriesUseCase).execute(eq(userId), any(LocalDate.class));
        verify(promptBuilder).build(any(), any(), any());
        verify(aiAssistant).generateWeeklyInsight(any());
        verify(reportRepo).save(any(WeeklyReport.class));
    }

    @Test
    void shouldThrowInsufficientDataExceptionWhenLessThan3Days() {
        // Given
        Long userId = 1L;
        LocalDate weekStart = LocalDate.of(2026, 4, 20);
        GenerateWeeklyReportCommand command = new GenerateWeeklyReportCommand(userId, weekStart, "USER");

        List<DailyEntryWithLogsResult> entries = List.of(
                DailyEntryWithLogsResult.builder()
                        .id(1L).userId(userId).date(LocalDate.of(2026, 4, 21))
                        .build()
        );

        WeeklyEntriesReportDto weeklyReport = WeeklyEntriesReportDto.builder()
                .userId(userId)
                .weekStart(weekStart)
                .weekEnd(weekStart.plusDays(6))
                .dailyEntries(entries)
                .build();

        when(getWeeklyEntriesUseCase.execute(eq(userId), any(LocalDate.class))).thenReturn(weeklyReport);

        // When/Then
        InsufficientDataException exception = assertThrows(
                InsufficientDataException.class,
                () -> useCase.execute(command)
        );

        assertTrue(exception.getMessage().contains("3 días"));
        verify(aiAssistant, never()).generateWeeklyInsight(any());
    }

    @Test
    void shouldUpdateExistingReport() {
        // Given
        Long userId = 1L;
        LocalDate weekStart = LocalDate.of(2026, 4, 20);
        GenerateWeeklyReportCommand command = new GenerateWeeklyReportCommand(userId, weekStart, "USER");

        WeeklyReport existingReport = new WeeklyReport(
                1L, userId, weekStart, weekStart.plusDays(6),
                "Old content", Instant.now()
        );

        List<DailyEntryWithLogsResult> entries = List.of(
                DailyEntryWithLogsResult.builder()
                        .id(1L).userId(userId).date(LocalDate.of(2026, 4, 21))
                        .studyLogs(List.of(StudyLogResponseDto.builder()
                                .id(1L).studied(true).hours(2.0f).subject("Java").build()))
                        .exerciseLogs(List.of(ExerciseLogResponseDto.builder()
                                .id(1L).exercised(true).hours(1.0f)
                                .muscularGroup(MuscularGroup.CHEST).energyLevel(80).build()))
                        .nutritionLogs(List.of(NutritionLogResponseDto.builder()
                                .id(1L).rating(NutritionRating.GOOD).metGoal(true).build()))
                        .moodLogs(List.of(MoodLogResponseDto.builder()
                                .id(1L).mood(MoodLevel.HAPPY).build()))
                        .sleepLogs(List.of(SleepLogResponseDto.builder()
                                .id(1L).hours(7.5f).quality(SleepQuality.GOOD).napped(false).build()))
                        .build(),
                DailyEntryWithLogsResult.builder()
                        .id(2L).userId(userId).date(LocalDate.of(2026, 4, 22))
                        .studyLogs(List.of(StudyLogResponseDto.builder()
                                .id(2L).studied(true).hours(3.0f).subject("Python").build()))
                        .build(),
                DailyEntryWithLogsResult.builder()
                        .id(3L).userId(userId).date(LocalDate.of(2026, 4, 23))
                        .studyLogs(List.of(StudyLogResponseDto.builder()
                                .id(3L).studied(false).skipReason("cansado").build()))
                        .build()
        );

        WeeklyEntriesReportDto weeklyReport = WeeklyEntriesReportDto.builder()
                .userId(userId)
                .weekStart(weekStart)
                .weekEnd(weekStart.plusDays(6))
                .dailyEntries(entries)
                .build();

        when(getWeeklyEntriesUseCase.execute(eq(userId), any(LocalDate.class))).thenReturn(weeklyReport);
        when(promptBuilder.build(any(), any(), any())).thenReturn("prompt test");
        when(aiAssistant.generateWeeklyInsight(any())).thenReturn("New report content");
        when(reportRepo.findByUserIdAndWeekStart(userId, weekStart)).thenReturn(java.util.Optional.of(existingReport));
        when(reportRepo.save(any(WeeklyReport.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        WeeklyReportResult result = useCase.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(reportRepo).save(any(WeeklyReport.class));
    }
}