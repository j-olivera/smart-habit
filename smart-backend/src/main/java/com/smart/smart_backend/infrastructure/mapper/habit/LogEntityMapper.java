package com.smart.smart_backend.infrastructure.mapper.habit;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;
import com.smart.smart_backend.application.dto.habit.log.ExerciseLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.MoodLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.NutritionLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.PersonalLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.SleepLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.StudyLogResponseDto;
import com.smart.smart_backend.domain.enums.MoodLevel;
import com.smart.smart_backend.domain.enums.MuscularGroup;
import com.smart.smart_backend.domain.enums.NutritionRating;
import com.smart.smart_backend.domain.enums.SleepQuality;
import com.smart.smart_backend.infrastructure.model.habit.DailyEntryEntity;
import com.smart.smart_backend.infrastructure.model.habit.ExerciseLogEntity;
import com.smart.smart_backend.infrastructure.model.habit.MoodLogEntity;
import com.smart.smart_backend.infrastructure.model.habit.NutritionLogEntity;
import com.smart.smart_backend.infrastructure.model.habit.PersonalLogEntity;
import com.smart.smart_backend.infrastructure.model.habit.SleepLogEntity;
import com.smart.smart_backend.infrastructure.model.habit.StudyLogEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LogEntityMapper {

    public StudyLogResponseDto toStudyDto(StudyLogEntity entity) {
        if (entity == null)
            return null;
        return new StudyLogResponseDto(
                entity.getId(),
                entity.getEntryId(),
                entity.getStudied(),
                entity.getHours(),
                entity.getSubject(),
                entity.getSkipReason());
    }

    public ExerciseLogResponseDto toExerciseDto(ExerciseLogEntity entity) {
        if (entity == null)
            return null;
        return new ExerciseLogResponseDto(
                entity.getId(),
                entity.getEntryId(),
                entity.getExercised(),
                entity.getHours(),
                MuscularGroup.valueOf(entity.getMuscleGroups()),
                entity.getEnergyLevel(),
                entity.getSkipReason());
    }

    public NutritionLogResponseDto toNutritionDto(NutritionLogEntity entity) {
        if (entity == null)
            return null;
        return new NutritionLogResponseDto(
                entity.getId(),
                entity.getEntryId(),
                NutritionRating.valueOf(entity.getRating()),
                entity.getHasObservations(),
                entity.getMetGoal());
    }

    public MoodLogResponseDto toMoodDto(MoodLogEntity entity) {
        if (entity == null)
            return null;
        return new MoodLogResponseDto(
                entity.getId(),
                entity.getEntryId(),
                MoodLevel.valueOf(entity.getMood()),
                entity.getHasObservations(),
                entity.getEventDescription(),
                entity.getSocialized(),
                entity.getSocialWith());
    }

    public SleepLogResponseDto toSleepDto(SleepLogEntity entity) {
        if (entity == null)
            return null;
        return new SleepLogResponseDto(
                entity.getId(),
                entity.getEntryId(),
                entity.getHours(),
                SleepQuality.valueOf(entity.getQuality()),
                entity.getNapped(),
                entity.getNapHours(),
                entity.getNapNeeded());
    }

    public PersonalLogResponseDto toPersonalDto(PersonalLogEntity entity) {
        if (entity == null)
            return null;
        return new PersonalLogResponseDto(
                entity.getId(),
                entity.getHabitId(),
                entity.getEntryId(),
                null,
                entity.getCompleted(),
                entity.getHours(),
                entity.getDescription());
    }

    public DailyEntryWithLogsResult toResult(
            DailyEntryEntity entry,
            StudyLogResponseDto studyLog,
            ExerciseLogResponseDto exerciseLog,
            NutritionLogResponseDto nutritionLog,
            MoodLogResponseDto moodLog,
            SleepLogResponseDto sleepLog,
            List<PersonalLogResponseDto> personalLogs) {
        return DailyEntryWithLogsResult.builder()
                .id(entry.getId())
                .userId(entry.getUserId())
                .date(entry.getDate())
                .studyLog(studyLog)
                .exerciseLog(exerciseLog)
                .nutritionLog(nutritionLog)
                .moodLog(moodLog)
                .sleepLog(sleepLog)
                .personalLogs(personalLogs)
                .build();
    }
}
