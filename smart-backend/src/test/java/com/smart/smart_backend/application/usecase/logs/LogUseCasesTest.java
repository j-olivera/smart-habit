package com.smart.smart_backend.application.usecase.logs;

import com.smart.smart_backend.application.dto.habit.log.StudyLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.StudyLogResponseDto;
import com.smart.smart_backend.application.port.out.logs.StudyLogRepositoryPort;
import com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort;
import com.smart.smart_backend.domain.model.habit.DailyEntry;
import com.smart.smart_backend.domain.model.habit.StudyLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogUseCasesTest {

    @Mock
    private DailyEntryRepositoryPort dailyEntryRepositoryPort;
    @Mock
    private StudyLogRepositoryPort studyLogRepositoryPort;

    private RegisterStudyLogUseCase registerStudyLogUseCase;

    @BeforeEach
    void setUp() {
        registerStudyLogUseCase = new RegisterStudyLogUseCase(dailyEntryRepositoryPort, studyLogRepositoryPort);
    }

    @Test
    void shouldRegisterNewStudyLog() {
        // Arrange
        Long userId = 1L;
        Long entryId = 20L;
        StudyLogRequestDto request = new StudyLogRequestDto(entryId, true, 2.0f, "Math", null);

        DailyEntry entry = DailyEntry.builder().id(entryId).userId(userId).date(LocalDate.now()).build();

        when(dailyEntryRepositoryPort.findByIdAndUserId(entryId, userId)).thenReturn(Optional.of(entry));
        when(studyLogRepositoryPort.findByEntryId(entryId)).thenReturn(Optional.empty());

        when(studyLogRepositoryPort.save(any(StudyLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        StudyLogResponseDto result = registerStudyLogUseCase.execute(userId, request);

        // Assert
        assertThat(result.studied()).isTrue();
        assertThat(result.subject()).isEqualTo("Math");
        verify(studyLogRepositoryPort).save(any(StudyLog.class));
    }

    @Test
    void shouldUpdateExistingStudyLog() {
        // Arrange
        Long userId = 1L;
        Long entryId = 20L;
        Long existingLogId = 500L;
        StudyLogRequestDto request = new StudyLogRequestDto(entryId, true, 3.0f, "Science", null);

        DailyEntry entry = DailyEntry.builder().id(entryId).userId(userId).date(LocalDate.now()).build();
        StudyLog existingLog = new StudyLog(existingLogId, entryId, true, 2.0f, "Math", null);

        when(dailyEntryRepositoryPort.findByIdAndUserId(entryId, userId)).thenReturn(Optional.of(entry));
        when(studyLogRepositoryPort.findByEntryId(entryId)).thenReturn(Optional.of(existingLog));

        when(studyLogRepositoryPort.save(any(StudyLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        StudyLogResponseDto result = registerStudyLogUseCase.execute(userId, request);

        // Assert
        assertThat(result.id()).isEqualTo(existingLogId); // Key check: ID must be preserved
        assertThat(result.subject()).isEqualTo("Science");
        assertThat(result.hours()).isEqualTo(3.0f);

        ArgumentCaptor<StudyLog> logCaptor = ArgumentCaptor.forClass(StudyLog.class);
        verify(studyLogRepositoryPort).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getId()).isEqualTo(existingLogId);
    }
}
