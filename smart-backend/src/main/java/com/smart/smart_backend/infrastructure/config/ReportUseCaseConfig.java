package com.smart.smart_backend.infrastructure.config;

import com.smart.smart_backend.application.port.in.habit.GetWeeklyEntriesUseCase;
import com.smart.smart_backend.application.port.in.report.GenerateWeeklyReportPort;
import com.smart.smart_backend.application.port.in.report.GetWeeklyReportByIdPort;
import com.smart.smart_backend.application.port.in.report.GetWeeklyReportsPort;
import com.smart.smart_backend.application.port.out.ai.AiAssistantPort;
import com.smart.smart_backend.application.port.out.ai.PromptBuilderPort;
import com.smart.smart_backend.application.port.out.report.WeeklyReportRepositoryPort;
import com.smart.smart_backend.application.usecase.report.GenerateWeeklyReportUseCase;
import com.smart.smart_backend.application.usecase.report.GetWeeklyReportByIdUseCase;
import com.smart.smart_backend.application.usecase.report.GetWeeklyReportsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportUseCaseConfig {

    @Bean
    public GenerateWeeklyReportPort generateWeeklyReportUseCase(
            GetWeeklyEntriesUseCase getWeeklyEntriesUseCase,
            AiAssistantPort aiAssistant,
            WeeklyReportRepositoryPort reportRepo,
            PromptBuilderPort promptBuilder) {
        return new GenerateWeeklyReportUseCase(
                getWeeklyEntriesUseCase,
                aiAssistant,
                reportRepo,
                promptBuilder
        );
    }

    @Bean
    public GetWeeklyReportsPort getWeeklyReportsUseCase(WeeklyReportRepositoryPort reportRepo) {
        return new GetWeeklyReportsUseCase(reportRepo);
    }

    @Bean
    public GetWeeklyReportByIdPort getWeeklyReportByIdUseCase(WeeklyReportRepositoryPort reportRepo) {
        return new GetWeeklyReportByIdUseCase(reportRepo);
    }
}