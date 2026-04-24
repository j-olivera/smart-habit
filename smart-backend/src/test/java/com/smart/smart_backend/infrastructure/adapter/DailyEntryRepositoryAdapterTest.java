package com.smart.smart_backend.infrastructure.adapter;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;
import com.smart.smart_backend.infrastructure.mapper.DailyEntryEntityMapper;
import com.smart.smart_backend.infrastructure.mapper.LogEntityMapper;
import com.smart.smart_backend.infrastructure.model.DailyEntryEntity;
import com.smart.smart_backend.infrastructure.model.StudyLogEntity;
import com.smart.smart_backend.infrastructure.repository.*;
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
                .habitId(10L)
                .studied(true)
                .hours(2.0f)
                .subject("Java")
                .build();

        when(jpaDailyEntryRepository.findAllByUserIdAndDateBetweenOrderByDateAsc(userId, start, end))
                .thenReturn(List.of(entryEntity));
        
        when(jpaStudyLogRepository.findAllByEntryIdIn(anyList())).thenReturn(List.of(studyLogEntity));
        when(jpaExerciseLogRepository.findAllByEntryIdIn(anyList())).thenReturn(List.of());
        when(jpaNutritionLogRepository.findAllByEntryIdIn(anyList())).thenReturn(List.of());
        when(jpaMoodLogRepository.findAllByEntryIdIn(anyList())).thenReturn(List.of());
        when(jpaSleepLogRepository.findAllByEntryIdIn(anyList())).thenReturn(List.of());

        // Act
        List<DailyEntryWithLogsResult> results = adapter.findWeeklyEntriesWithLogs(userId, start, end);

        // Assert
        assertThat(results).hasSize(1);
        DailyEntryWithLogsResult first = results.get(0);
        assertThat(first.id()).isEqualTo(100L);
        assertThat(first.studyLogs()).hasSize(1);
        assertThat(first.studyLogs().get(0).subject()).isEqualTo("Java");
    }
}
