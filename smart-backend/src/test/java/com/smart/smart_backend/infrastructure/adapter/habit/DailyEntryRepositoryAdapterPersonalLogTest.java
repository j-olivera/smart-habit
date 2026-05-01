package com.smart.smart_backend.infrastructure.adapter.habit;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;
import com.smart.smart_backend.infrastructure.mapper.habit.DailyEntryEntityMapper;
import com.smart.smart_backend.infrastructure.mapper.habit.LogEntityMapper;
import com.smart.smart_backend.infrastructure.model.habit.DailyEntryEntity;
import com.smart.smart_backend.infrastructure.model.habit.HabitEntity;
import com.smart.smart_backend.infrastructure.model.habit.PersonalLogEntity;
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
class DailyEntryRepositoryAdapterPersonalLogTest {

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
    void shouldEnrichPersonalLogWithHabitName() {
        // Arrange
        Long userId = 1L;
        LocalDate date = LocalDate.of(2026, 4, 21);
        Long habitId = 10L;

        DailyEntryEntity entryEntity = DailyEntryEntity.builder()
                .id(100L)
                .userId(userId)
                .date(date)
                .createdAt(LocalDateTime.now())
                .build();

        PersonalLogEntity personalLog = PersonalLogEntity.builder()
                .id(1L)
                .entryId(100L)
                .habitId(habitId)
                .completed(true)
                .hours(1.5f)
                .description("Meditation")
                .build();

        HabitEntity habitEntity = HabitEntity.builder()
                .id(habitId)
                .name("Daily Meditation")
                .build();

        when(jpaDailyEntryRepository.findByUserIdAndDate(userId, date))
                .thenReturn(Optional.of(entryEntity));
        when(jpaPersonalLogRepository.findAllByEntryIdIn(anyList())).thenReturn(List.of(personalLog));
        when(jpaHabitRepository.findById(habitId)).thenReturn(Optional.of(habitEntity));

        // Mocking other logs as empty
        when(jpaStudyLogRepository.findByEntryId(100L)).thenReturn(Optional.empty());
        when(jpaExerciseLogRepository.findByEntryId(100L)).thenReturn(Optional.empty());
        when(jpaNutritionLogRepository.findByEntryId(100L)).thenReturn(Optional.empty());
        when(jpaMoodLogRepository.findByEntryId(100L)).thenReturn(Optional.empty());
        when(jpaSleepLogRepository.findByEntryId(100L)).thenReturn(Optional.empty());

        // Act
        Optional<DailyEntryWithLogsResult> result = adapter.findByUserIdAndDateWithLogs(userId, date);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().personalLogs()).hasSize(1);
        assertThat(result.get().personalLogs().get(0).habitName()).isEqualTo("Daily Meditation");
    }
}
