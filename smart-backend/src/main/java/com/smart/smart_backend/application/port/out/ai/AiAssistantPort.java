package com.smart.smart_backend.application.port.out.ai;

public interface AiAssistantPort {
    String generateWeeklyInsight(String structuredPrompt);
}