package com.smart.smart_backend.infrastructure.mapper;

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
import com.smart.smart_backend.infrastructure.model.DailyEntryEntity;
import com.smart.smart_backend.infrastructure.model.ExerciseLogEntity;
import com.smart.smart_backend.infrastructure.model.MoodLogEntity;
import com.smart.smart_backend.infrastructure.model.NutritionLogEntity;
import com.smart.smart_backend.infrastructure.model.SleepLogEntity;
import com.smart.smart_backend.infrastructure.model.StudyLogEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LogEntityMapper {

    public StudyLogResponseDto toStudyDto(StudyLogEntity entity) {
        if (entity == null) return null;
        return new StudyLogResponseDto(
                entity.getId(),
                entity.getHabitId(),
                entity.getEntryId(),
                entity.getStudied(),
                entity.getHours(),
                entity.getSubject(),
                entity.getSkipReason()
        );
    }

    public ExerciseLogResponseDto toExerciseDto(ExerciseLogEntity entity) {
        if (entity == null) return null;
        return new ExerciseLogResponseDto(
                entity.getId(),
                entity.getHabitId(),
                entity.getEntryId(),
                entity.getExercised(),
                entity.getHours(),
                MuscularGroup.valueOf(entity.getMuscleGroups()),
                entity.getEnergyLevel(),
                entity.getSkipReason()
        );
    }

    public NutritionLogResponseDto toNutritionDto(NutritionLogEntity entity) {
        if (entity == null) return null;
        return new NutritionLogResponseDto(
                entity.getId(),
                entity.getHabitId(),
                entity.getEntryId(),
                NutritionRating.valueOf(entity.getRating()),
                entity.getHasObservations(),
                entity.getMetGoal()
        );
    }

    public MoodLogResponseDto toMoodDto(MoodLogEntity entity) {
        if (entity == null) return null;
        return new MoodLogResponseDto(
                entity.getId(),
                entity.getHabitId(),
                entity.getEntryId(),
                MoodLevel.valueOf(entity.getMood()),
                entity.getHasObservations(),
                entity.getEventDescription(),
                entity.getSocialized(),
                entity.getSocialWith()
        );
    }

    public SleepLogResponseDto toSleepDto(SleepLogEntity entity) {
        if (entity == null) return null;
        return new SleepLogResponseDto(
                entity.getId(),
                entity.getHabitId(),
                entity.getEntryId(),
                entity.getHours(),
                SleepQuality.valueOf(entity.getQuality()),
                entity.getNapped(),
                entity.getNapHours(),
                entity.getNapNeeded()
        );
    }

    public DailyEntryWithLogsResult toResult(
            DailyEntryEntity entry,
            List<StudyLogResponseDto> studyLogs,
            List<ExerciseLogResponseDto> exerciseLogs,
            List<NutritionLogResponseDto> nutritionLogs,
            List<MoodLogResponseDto> moodLogs,
            List<SleepLogResponseDto> sleepLogs) {
        return DailyEntryWithLogsResult.builder()
                .id(entry.getId())
                .userId(entry.getUserId())
                .date(entry.getDate())
                .studyLogs(studyLogs)
                .exerciseLogs(exerciseLogs)
                .nutritionLogs(nutritionLogs)
                .moodLogs(moodLogs)
                .sleepLogs(sleepLogs)
                .build();
    }
}
