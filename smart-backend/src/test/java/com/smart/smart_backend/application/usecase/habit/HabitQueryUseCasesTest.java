package com.smart.smart_backend.application.usecase.habit;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;
import com.smart.smart_backend.application.dto.habit.WeeklyEntriesReportDto;
import com.smart.smart_backend.application.port.out.habit.DailyEntryRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HabitQueryUseCasesTest {

    @Mock
    private DailyEntryRepositoryPort dailyEntryRepositoryPort;

    private GetDailyEntryService getDailyEntryService;
    private GetWeeklyEntriesService getWeeklyEntriesService;

    @BeforeEach
    void setUp() {
        getDailyEntryService = new GetDailyEntryService(dailyEntryRepositoryPort);
        getWeeklyEntriesService = new GetWeeklyEntriesService(dailyEntryRepositoryPort);
    }

    @Test
    void shouldGetDailyEntryByDate() {
        // Arrange
        Long userId = 1L;
        LocalDate date = LocalDate.now();
        DailyEntryWithLogsResult expectedResult = DailyEntryWithLogsResult.builder()
                .id(100L)
                .userId(userId)
                .date(date)
                .build();

        when(dailyEntryRepositoryPort.findByUserIdAndDateWithLogs(userId, date))
                .thenReturn(Optional.of(expectedResult));

        // Act
        DailyEntryWithLogsResult result = getDailyEntryService.execute(userId, date);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(100L);
    }

    @Test
    void shouldReturnNullWhenDailyEntryNotFound() {
        // Arrange
        Long userId = 1L;
        LocalDate date = LocalDate.now();
        when(dailyEntryRepositoryPort.findByUserIdAndDateWithLogs(userId, date))
                .thenReturn(Optional.empty());

        // Act
        DailyEntryWithLogsResult result = getDailyEntryService.execute(userId, date);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void shouldGetWeeklyReportWithNormalizedDates() {
        // Arrange
        Long userId = 1L;
        // Wednesday, April 22, 2026
        LocalDate dateInMiddleOfWeek = LocalDate.of(2026, 4, 22);
        LocalDate expectedMonday = LocalDate.of(2026, 4, 20);
        LocalDate expectedSunday = LocalDate.of(2026, 4, 26);

        DailyEntryWithLogsResult entry1 = DailyEntryWithLogsResult.builder().date(expectedMonday).build();

        when(dailyEntryRepositoryPort.findWeeklyEntriesWithLogs(userId, expectedMonday, expectedSunday))
                .thenReturn(List.of(entry1));

        // Act
        WeeklyEntriesReportDto result = getWeeklyEntriesService.execute(userId, dateInMiddleOfWeek);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.weekStart()).isEqualTo(expectedMonday);
        assertThat(result.weekEnd()).isEqualTo(expectedSunday);
        assertThat(result.dailyEntries()).hasSize(1);
        assertThat(result.dailyEntries().get(0).date()).isEqualTo(expectedMonday);
    }
}
