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
            HabitRepositoryPort habitRepositoryPort,
            DailyEntryRepositoryPort dailyEntryRepositoryPort,
            StudyLogRepositoryPort studyLogRepositoryPort) {
        return new RegisterStudyLogUseCase(habitRepositoryPort, dailyEntryRepositoryPort, studyLogRepositoryPort);
    }

    @Bean
    public RegisterExerciseLogUseCase registerExerciseLogUseCase(
            HabitRepositoryPort habitRepositoryPort,
            DailyEntryRepositoryPort dailyEntryRepositoryPort,
            ExerciseLogRepositoryPort exerciseLogRepositoryPort) {
        return new RegisterExerciseLogUseCase(habitRepositoryPort, dailyEntryRepositoryPort, exerciseLogRepositoryPort);
    }

    @Bean
    public RegisterMoodLogUseCase registerMoodLogUseCase(
            HabitRepositoryPort habitRepositoryPort,
            DailyEntryRepositoryPort dailyEntryRepositoryPort,
            MoodLogRepositoryPort moodLogRepositoryPort) {
        return new RegisterMoodLogUseCase(habitRepositoryPort, dailyEntryRepositoryPort, moodLogRepositoryPort);
    }

    @Bean
    public RegisterNutritionLogUseCase registerNutritionLogUseCase(
            HabitRepositoryPort habitRepositoryPort,
            DailyEntryRepositoryPort dailyEntryRepositoryPort,
            NutritionLogRepositoryPort nutritionLogRepositoryPort) {
        return new RegisterNutritionLogUseCase(habitRepositoryPort, dailyEntryRepositoryPort, nutritionLogRepositoryPort);
    }

    @Bean
    public RegisterSleepLogUseCase registerSleepLogUseCase(
            HabitRepositoryPort habitRepositoryPort,
            DailyEntryRepositoryPort dailyEntryRepositoryPort,
            SleepLogRepositoryPort sleepLogRepositoryPort) {
        return new RegisterSleepLogUseCase(habitRepositoryPort, dailyEntryRepositoryPort, sleepLogRepositoryPort);
    }
}
