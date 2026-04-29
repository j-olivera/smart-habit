package com.smart.smart_backend.infrastructure.scheduler;

import com.smart.smart_backend.application.port.in.report.GenerateWeeklyReportCommand;
import com.smart.smart_backend.application.port.in.report.GenerateWeeklyReportPort;
import com.smart.smart_backend.infrastructure.repository.user.JpaUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

@Component
public class WeeklyReportScheduler {

    private static final Logger log = LoggerFactory.getLogger(WeeklyReportScheduler.class);

    private final GenerateWeeklyReportPort generateReport;
    private final JpaUserRepository userRepository;

    public WeeklyReportScheduler(GenerateWeeklyReportPort generateReport, JpaUserRepository userRepository) {
        this.generateReport = generateReport;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 59 23 * * SUN") // Domingos 23:59:00
    public void generateReportsForAllUsers() {
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        log.info("Starting scheduled weekly report generation for week: {}", weekStart);

        userRepository.findByActiveTrue().forEach(user ->
            CompletableFuture.runAsync(() -> {
                try {
                    log.info("Generating report for user: {}", user.getId());
                    generateReport.execute(new GenerateWeeklyReportCommand(
                        user.getId(),
                        weekStart,
                        "SCHEDULER"
                    ));
                    log.info("Report generated successfully for user: {}", user.getId());
                } catch (Exception e) {
                    log.error("Failed to generate report for user {}: {}", user.getId(), e.getMessage());
                }
            })
        );
    }
}