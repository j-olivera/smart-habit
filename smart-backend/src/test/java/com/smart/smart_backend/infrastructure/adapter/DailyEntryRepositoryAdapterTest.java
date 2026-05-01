package com.smart.smart_backend.infrastructure.adapter;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;
import com.smart.smart_backend.infrastructure.adapter.habit.DailyEntryRepositoryAdapter;
import com.smart.smart_backend.infrastructure.mapper.habit.DailyEntryEntityMapper;
import com.smart.smart_backend.infrastructure.mapper.habit.LogEntityMapper;
import com.smart.smart_backend.infrastructure.model.habit.DailyEntryEntity;
import com.smart.smart_backend.infrastructure.model.habit.ExerciseLogEntity;
import com.smart.smart_backend.infrastructure.model.habit.StudyLogEntity;
import com.smart.smart_backend.infrastructure.repository.habit.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyEntryRepositoryAdapterTest {

    @Mock private JpaDailyEntryRepository jpaDailyEntryRepository;
    @Mock private JpaStudyLogRepository jpaStudyLogRepository;
    @Mock private JpaExerciseLogRepository jpaExerciseLogRepository;
    @Mock private JpaNutritionLogRepository jpaNutritionLogRepository;
    @Mock private JpaMoodLogRepository jpaMoodLogRepository;
    @Mock private JpaSleepLogRepository jpaSleepLogRepository;
    @Mock private JpaPersonalLogRepository jpaPersonalLogRepository;
    @Mock private JpaHabitRepository jpaHabitRepository;

    private DailyEntryRepositoryAdapter adapter;
    private LogEntityMapper logEntityMapper;
    private DailyEntryEntityMapper dailyEntryEntityMapper;

    @BeforeEach
    void setUp() {
        logEntityMapper = new LogEntityMapper();
        dailyEntryEntityMapper = new DailyEntryEntityMapper();
        adapter = new DailyEntryRepositoryAdapter(
                jpaDailyEntryRepository,
                jpaStudyLogRepository,
                jpaExerciseLogRepository,
                jpaNutritionLogRepository,
                jpaMoodLogRepository,
                jpaSleepLogRepository,
                jpaPersonalLogRepository,
                jpaHabitRepository,
                dailyEntryEntityMapper,
                logEntityMapper
        );
    }

    @Test
    void shouldFindWeeklyEntriesWithLogsOptimized() {
        // Arrange
        Long userId = 1L;
        LocalDate start = LocalDate.of(2026, 4, 20);
        LocalDate end = LocalDate.of(2026, 4, 26);
        
        DailyEntryEntity entryEntity = DailyEntryEntity.builder()
                .id(100L)
                .userId(userId)
                .date(start)
                .createdAt(LocalDateTime.now())
                .build();
        
        StudyLogEntity studyLogEntity = StudyLogEntity.builder()
                .id(500L)
                .entryId(100L)
                .studied(true)
                .hours(2.0f)
                .subject("Java")
                .build();

        com.smart.smart_backend.infrastructure.model.habit.PersonalLogEntity personalLogEntity = com.smart.smart_backend.infrastructure.model.habit.PersonalLogEntity.builder()
                .id(600L)
                .entryId(100L)
                .habitId(10L)
                .completed(true)
                .hours(1.0f)
                .description("Read")
                .build();

        com.smart.smart_backend.infrastructure.model.habit.HabitEntity habitEntity = com.smart.smart_backend.infrastructure.model.habit.HabitEntity.builder()
                .id(10L)
                .name("Reading")
                .build();

        when(jpaDailyEntryRepository.findAllByUserIdAndDateBetweenOrderByDateAsc(userId, start, end))
                .thenReturn(List.of(entryEntity));
        
        when(jpaStudyLogRepository.findAllByEntryIdIn(anyList())).thenReturn(List.of(studyLogEntity));
        when(jpaExerciseLogRepository.findAllByEntryIdIn(anyList())).thenReturn(List.of());
        when(jpaNutritionLogRepository.findAllByEntryIdIn(anyList())).thenReturn(List.of());
        when(jpaMoodLogRepository.findAllByEntryIdIn(anyList())).thenReturn(List.of());
        when(jpaSleepLogRepository.findAllByEntryIdIn(anyList())).thenReturn(List.of());
        when(jpaPersonalLogRepository.findAllByEntryIdIn(anyList())).thenReturn(List.of(personalLogEntity));
        when(jpaHabitRepository.findById(10L)).thenReturn(Optional.of(habitEntity));

        // Act
        List<DailyEntryWithLogsResult> results = adapter.findWeeklyEntriesWithLogs(userId, start, end);

        // Assert
        assertThat(results).hasSize(1);
        DailyEntryWithLogsResult first = results.get(0);
        assertThat(first.id()).isEqualTo(100L);
        assertThat(first.studyLog()).isNotNull();
        assertThat(first.studyLog().subject()).isEqualTo("Java");
        assertThat(first.personalLogs()).hasSize(1);
        assertThat(first.personalLogs().get(0).habitName()).isEqualTo("Reading");
    }

    @Test
    void shouldReturnEmptyListWhenNoEntriesInRange() {
        // Arrange
        Long userId = 1L;
        LocalDate start = LocalDate.of(2026, 4, 20);
        LocalDate end = LocalDate.of(2026, 4, 26);

        when(jpaDailyEntryRepository.findAllByUserIdAndDateBetweenOrderByDateAsc(userId, start, end))
                .thenReturn(List.of());

        // Act
        List<DailyEntryWithLogsResult> results = adapter.findWeeklyEntriesWithLogs(userId, start, end);

        // Assert
        assertThat(results).isEmpty();
    }

    @Test
    void shouldFindByUserIdAndDateWithLogs() {
        // Arrange
        Long userId = 1L;
        LocalDate date = LocalDate.of(2026, 4, 21);

        DailyEntryEntity entryEntity = DailyEntryEntity.builder()
                .id(100L)
                .userId(userId)
                .date(date)
                .createdAt(LocalDateTime.now())
                .build();

        StudyLogEntity studyLog = StudyLogEntity.builder()
                .id(1L)
                .entryId(100L)
                .studied(true)
                .hours(2.0f)
                .subject("Java")
                .build();

        when(jpaDailyEntryRepository.findByUserIdAndDate(userId, date))
                .thenReturn(Optional.of(entryEntity));
        when(jpaStudyLogRepository.findByEntryId(100L)).thenReturn(Optional.of(studyLog));
        when(jpaExerciseLogRepository.findByEntryId(100L)).thenReturn(Optional.empty());
        when(jpaNutritionLogRepository.findByEntryId(100L)).thenReturn(Optional.empty());
        when(jpaMoodLogRepository.findByEntryId(100L)).thenReturn(Optional.empty());
        when(jpaSleepLogRepository.findByEntryId(100L)).thenReturn(Optional.empty());
        when(jpaPersonalLogRepository.findAllByEntryIdIn(anyList())).thenReturn(List.of());

        // Act
        Optional<DailyEntryWithLogsResult> result = adapter.findByUserIdAndDateWithLogs(userId, date);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(100L);
        assertThat(result.get().studyLog()).isNotNull();
    }

    @Test
    void shouldReturnEmptyWhenNoEntryForDate() {
        // Arrange
        Long userId = 1L;
        LocalDate date = LocalDate.of(2026, 4, 21);

        when(jpaDailyEntryRepository.findByUserIdAndDate(userId, date))
                .thenReturn(Optional.empty());

        // Act
        Optional<DailyEntryWithLogsResult> result = adapter.findByUserIdAndDateWithLogs(userId, date);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleMultipleEntriesWithMultipleLogs() {
        // Arrange
        Long userId = 1L;
        LocalDate start = LocalDate.of(2026, 4, 20);
        LocalDate end = LocalDate.of(2026, 4, 26);

        List<DailyEntryEntity> entries = List.of(
                DailyEntryEntity.builder().id(1L).userId(userId).date(start).createdAt(LocalDateTime.now()).build(),
                DailyEntryEntity.builder().id(2L).userId(userId).date(start.plusDays(1)).createdAt(LocalDateTime.now()).build()
        );

        List<StudyLogEntity> studyLogs = List.of(
                StudyLogEntity.builder().id(1L).entryId(1L).studied(true).hours(2.0f).subject("Java").build(),
                StudyLogEntity.builder().id(2L).entryId(2L).studied(true).hours(3.0f).subject("Python").build()
        );

        List<ExerciseLogEntity> exerciseLogs = List.of(
                ExerciseLogEntity.builder().id(1L).entryId(1L).exercised(true).hours(1.0f).muscleGroups("CHEST").build(),
                ExerciseLogEntity.builder().id(2L).entryId(2L).exercised(true).hours(1.5f).muscleGroups("LEGS").build()
        );

        com.smart.smart_backend.infrastructure.model.habit.PersonalLogEntity personalLogEntity = com.smart.smart_backend.infrastructure.model.habit.PersonalLogEntity.builder()
                .id(600L)
                .entryId(1L)
                .habitId(10L)
                .completed(true)
                .hours(1.0f)
                .description("Read")
                .build();

        com.smart.smart_backend.infrastructure.model.habit.HabitEntity habitEntity = com.smart.smart_backend.infrastructure.model.habit.HabitEntity.builder()
                .id(10L)
                .name("Reading")
                .build();

        when(jpaDailyEntryRepository.findAllByUserIdAndDateBetweenOrderByDateAsc(userId, start, end))
                .thenReturn(entries);
        when(jpaStudyLogRepository.findAllByEntryIdIn(anyList())).thenReturn(studyLogs);
        when(jpaExerciseLogRepository.findAllByEntryIdIn(anyList())).thenReturn(exerciseLogs);
        when(jpaNutritionLogRepository.findAllByEntryIdIn(anyList())).thenReturn(List.of());
        when(jpaMoodLogRepository.findAllByEntryIdIn(anyList())).thenReturn(List.of());
        when(jpaSleepLogRepository.findAllByEntryIdIn(anyList())).thenReturn(List.of());
        when(jpaPersonalLogRepository.findAllByEntryIdIn(anyList())).thenReturn(List.of(personalLogEntity));
        when(jpaHabitRepository.findById(10L)).thenReturn(Optional.of(habitEntity));

        // Act
        List<DailyEntryWithLogsResult> results = adapter.findWeeklyEntriesWithLogs(userId, start, end);

        // Assert
        assertThat(results).hasSize(2);
        assertThat(results.get(0).studyLog()).isNotNull();
        assertThat(results.get(0).exerciseLog()).isNotNull();
        assertThat(results.get(0).personalLogs()).hasSize(1);
        assertThat(results.get(0).personalLogs().get(0).habitName()).isEqualTo("Reading");
        assertThat(results.get(1).studyLog()).isNotNull();
        assertThat(results.get(1).personalLogs()).isEmpty();
    }
}
