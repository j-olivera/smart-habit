package com.smart.smart_backend.infrastructure.config;

import com.smart.smart_backend.application.port.out.habit.DailyEntryRepositoryPort;
import com.smart.smart_backend.application.port.out.habit.HabitRepositoryPort;
import com.smart.smart_backend.application.usecase.habit.*;
import com.smart.smart_backend.application.port.in.registers.CreateDailyEntry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HabitUseCaseConfig {

    @Bean
    public RegisterHabitUseCase registerHabitUseCase(HabitRepositoryPort repositoryPort) {
        return new RegisterHabitUseCase(repositoryPort);
    }

    @Bean
    public GetUserHabitsUseCase getUserHabitsUseCase(HabitRepositoryPort repositoryPort) {
        return new GetUserHabitsUseCase(repositoryPort);
    }

    @Bean
    public DesactivateHabitUseCase desactivateHabitUseCase(HabitRepositoryPort repositoryPort) {
        return new DesactivateHabitUseCase(repositoryPort);
    }

    @Bean
    public GetDailyEntryService getDailyEntryService(DailyEntryRepositoryPort dailyEntryRepositoryPort) {
        return new GetDailyEntryService(dailyEntryRepositoryPort);
    }

    @Bean
    public GetWeeklyEntriesService getWeeklyEntriesService(DailyEntryRepositoryPort dailyEntryRepositoryPort) {
        return new GetWeeklyEntriesService(dailyEntryRepositoryPort);
    }

    @Bean
    public CreateDailyEntryService createDailyEntryUseCase(com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort repositoryPort) {
        return new CreateDailyEntryService(repositoryPort);
    }
}
