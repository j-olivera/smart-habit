package com.smart.smart_backend.application.dto.habit;

import com.smart.smart_backend.application.dto.habit.log.ExerciseLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.MoodLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.NutritionLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.SleepLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.StudyLogResponseDto;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record DailyEntryWithLogsResult(
    Long id,
    Long userId,
    LocalDate date,
    List<StudyLogResponseDto> studyLogs,
    List<ExerciseLogResponseDto> exerciseLogs,
    List<NutritionLogResponseDto> nutritionLogs,
    List<MoodLogResponseDto> moodLogs,
    List<SleepLogResponseDto> sleepLogs
) {}
