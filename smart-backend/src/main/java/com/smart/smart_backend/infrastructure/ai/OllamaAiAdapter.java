package com.smart.smart_backend.infrastructure.ai;

import com.smart.smart_backend.application.port.out.ai.AiAssistantPort;
import com.smart.smart_backend.domain.exception.ReportGenerationException;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

@Component
public class OllamaAiAdapter implements AiAssistantPort {

    private final ChatModel chatModel;

    public OllamaAiAdapter(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String generateWeeklyInsight(String structuredPrompt) {
        try {
            return chatModel.call(new Prompt(structuredPrompt))
                    .getResult()
                    .getOutput()
                    .getText();
        } catch (Exception e) {
            throw new ReportGenerationException(
                    "Error al conectar con Ollama: " + e.getMessage(), e
            );
        }
    }
}