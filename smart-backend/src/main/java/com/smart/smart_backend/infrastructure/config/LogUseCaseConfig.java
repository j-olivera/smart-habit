package com.smart.smart_backend.infrastructure.config;

import com.smart.smart_backend.application.port.out.habit.HabitRepositoryPort;
import com.smart.smart_backend.application.port.out.logs.*;
import com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort;
import com.smart.smart_backend.application.usecase.logs.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogUseCaseConfig {

    @Bean
    public RegisterStudyLogUseCase registerStudyLogUseCase(
            DailyEntryRepositoryPort dailyEntryRepositoryPort,
            StudyLogRepositoryPort studyLogRepositoryPort) {
        return new RegisterStudyLogUseCase(dailyEntryRepositoryPort, studyLogRepositoryPort);
    }

    @Bean
    public RegisterExerciseLogUseCase registerExerciseLogUseCase(
            DailyEntryRepositoryPort dailyEntryRepositoryPort,
            ExerciseLogRepositoryPort exerciseLogRepositoryPort) {
        return new RegisterExerciseLogUseCase(dailyEntryRepositoryPort, exerciseLogRepositoryPort);
    }

    @Bean
    public RegisterMoodLogUseCase registerMoodLogUseCase(
            DailyEntryRepositoryPort dailyEntryRepositoryPort,
            MoodLogRepositoryPort moodLogRepositoryPort) {
        return new RegisterMoodLogUseCase(dailyEntryRepositoryPort, moodLogRepositoryPort);
    }

    @Bean
    public RegisterNutritionLogUseCase registerNutritionLogUseCase(
            DailyEntryRepositoryPort dailyEntryRepositoryPort,
            NutritionLogRepositoryPort nutritionLogRepositoryPort) {
        return new RegisterNutritionLogUseCase(dailyEntryRepositoryPort, nutritionLogRepositoryPort);
    }

    @Bean
    public RegisterSleepLogUseCase registerSleepLogUseCase(
            DailyEntryRepositoryPort dailyEntryRepositoryPort,
            SleepLogRepositoryPort sleepLogRepositoryPort) {
        return new RegisterSleepLogUseCase(dailyEntryRepositoryPort, sleepLogRepositoryPort);
    }

    @Bean
    public RegisterPersonalLogUseCase registerPersonalLogUseCase(
            HabitRepositoryPort habitRepositoryPort,
            DailyEntryRepositoryPort dailyEntryRepositoryPort,
            PersonalLogRepositoryPort personalLogRepositoryPort) {
        return new RegisterPersonalLogUseCase(habitRepositoryPort, dailyEntryRepositoryPort, personalLogRepositoryPort);
    }
}
