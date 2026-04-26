package com.smart.smart_backend.domain.model.report;

import java.time.Instant;
import java.time.LocalDate;

public class WeeklyReport {

    private Long id;
    private Long userId;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private String aiContent;
    private Instant generatedAt;

    public WeeklyReport() {
    }

    public WeeklyReport(Long id, Long userId, LocalDate weekStart, LocalDate weekEnd, String aiContent, Instant generatedAt) {
        this.id = id;
        this.userId = userId;
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.aiContent = aiContent;
        this.generatedAt = generatedAt;
    }

    public static WeeklyReport create(Long userId, LocalDate weekStart, LocalDate weekEnd, String aiContent) {
        return new WeeklyReport(
            null,
            userId,
            weekStart,
            weekEnd,
            aiContent,
            Instant.now()
        );
    }

    public WeeklyReport update(String aiContent) {
        this.aiContent = aiContent;
        this.generatedAt = Instant.now();
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getWeekStart() {
        return weekStart;
    }

    public void setWeekStart(LocalDate weekStart) {
        this.weekStart = weekStart;
    }

    public LocalDate getWeekEnd() {
        return weekEnd;
    }

    public void setWeekEnd(LocalDate weekEnd) {
        this.weekEnd = weekEnd;
    }

    public String getAiContent() {
        return aiContent;
    }

    public void setAiContent(String aiContent) {
        this.aiContent = aiContent;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }
}