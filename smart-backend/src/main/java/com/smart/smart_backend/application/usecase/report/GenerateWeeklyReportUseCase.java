package com.smart.smart_backend.application.usecase.report;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;
import com.smart.smart_backend.application.dto.report.WeeklyReportResult;
import com.smart.smart_backend.application.port.in.habit.GetWeeklyEntriesUseCase;
import com.smart.smart_backend.application.port.in.report.GenerateWeeklyReportCommand;
import com.smart.smart_backend.application.port.in.report.GenerateWeeklyReportPort;
import com.smart.smart_backend.application.port.out.ai.AiAssistantPort;
import com.smart.smart_backend.application.port.out.ai.PromptBuilderPort;
import com.smart.smart_backend.application.port.out.report.WeeklyReportRepositoryPort;
import com.smart.smart_backend.domain.exception.InsufficientDataException;
import com.smart.smart_backend.domain.model.report.WeeklyReport;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class GenerateWeeklyReportUseCase implements GenerateWeeklyReportPort {

    private static final int MIN_DAYS_REQUIRED = 3;

    private final GetWeeklyEntriesUseCase getWeeklyEntriesUseCase;
    private final AiAssistantPort aiAssistant;
    private final WeeklyReportRepositoryPort reportRepo;
    private final PromptBuilderPort promptBuilder;

    public GenerateWeeklyReportUseCase(
            GetWeeklyEntriesUseCase getWeeklyEntriesUseCase,
            AiAssistantPort aiAssistant,
            WeeklyReportRepositoryPort reportRepo,
            PromptBuilderPort promptBuilder) {
        this.getWeeklyEntriesUseCase = getWeeklyEntriesUseCase;
        this.aiAssistant = aiAssistant;
        this.reportRepo = reportRepo;
        this.promptBuilder = promptBuilder;
    }

    @Override
    public WeeklyReportResult execute(GenerateWeeklyReportCommand cmd) {
        // 1. Normalizar weekStart al lunes de esa semana
        LocalDate weekStart = cmd.weekStart().with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        // 2. Obtener entradas de la semana
        var weeklyReport = getWeeklyEntriesUseCase.execute(cmd.userId(), weekStart);
        List<DailyEntryWithLogsResult> entries = weeklyReport.dailyEntries();

        // 3. Validar mínimo de días con datos
        if (entries.size() < MIN_DAYS_REQUIRED) {
            throw new InsufficientDataException(entries.size(), MIN_DAYS_REQUIRED);
        }

        // 4. Construir el prompt estructurado
        String prompt = promptBuilder.build(entries, weekStart, weekEnd);

        // 5. Llamar al modelo
        String aiContent = aiAssistant.generateWeeklyInsight(prompt);

        // 6. Construir entidad de dominio (upsert)
        WeeklyReport report = reportRepo
                .findByUserIdAndWeekStart(cmd.userId(), weekStart)
                .map(existing -> existing.update(aiContent))
                .orElseGet(() -> WeeklyReport.create(cmd.userId(), weekStart, weekEnd, aiContent));

        // 7. Persistir
        WeeklyReport saved = reportRepo.save(report);

        return new WeeklyReportResult(
                saved.getId(),
                saved.getWeekStart(),
                saved.getWeekEnd(),
                saved.getAiContent(),
                saved.getGeneratedAt()
        );
    }
}