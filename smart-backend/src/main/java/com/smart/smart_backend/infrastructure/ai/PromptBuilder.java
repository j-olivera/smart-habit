package com.smart.smart_backend.infrastructure.ai;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;
import com.smart.smart_backend.application.dto.habit.log.ExerciseLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.MoodLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.NutritionLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.SleepLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.StudyLogResponseDto;
import com.smart.smart_backend.application.port.out.ai.PromptBuilderPort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class PromptBuilder implements PromptBuilderPort {

    private static final String SYSTEM_PROMPT = """
        Eres un coach de hábitos saludables, empático y motivador.
        Se te proporcionan los registros diarios de hábitos de un usuario durante una semana.
        
        Tu tarea es generar un reporte semanal con EXACTAMENTE estas secciones,
        usando los títulos tal cual están escritos:
        
        ## RESUMEN GENERAL
        (2-3 oraciones describiendo cómo fue la semana en general)
        
        ## FORTALEZAS
        (máximo 3 puntos concretos basados en los datos)
        
        ## ÁREAS DE MEJORA
        (máximo 3 puntos concretos y accionables)
        
        ## RIESGOS DETECTADOS
        (si los hay — patrones preocupantes como poco sueño sostenido, inactividad, etc.)
        (si no hay riesgos, escribir: "Sin riesgos detectados esta semana.")
        
        ## RECOMENDACIONES
        (máximo 3 recomendaciones concretas y personalizadas para la próxima semana)
        
        REGLAS:
        - Responde SIEMPRE en español.
        - Sé específico con los datos provistos. No inventes información.
        - Si un hábito no aparece registrado un día, NO asumas que no se realizó.
        - No uses lenguaje clínico ni alarmista.
        - No salgas del formato de secciones indicado.
        """;

    @Override
    public String build(List<DailyEntryWithLogsResult> entries, LocalDate weekStart, LocalDate weekEnd) {
        StringBuilder data = new StringBuilder();
        data.append("Semana del %s al %s:\n\n".formatted(weekStart, weekEnd));

        for (DailyEntryWithLogsResult entry : entries) {
            data.append("--- %s ---\n".formatted(entry.date()));
            serializeStudy(entry.studyLog(), data);
            serializeExercise(entry.exerciseLog(), data);
            serializeNutrition(entry.nutritionLog(), data);
            serializeMood(entry.moodLog(), data);
            serializeSleep(entry.sleepLog(), data);
            serializePersonal(entry.personalLogs(), data);
            data.append("\n");
        }

        return SYSTEM_PROMPT + "\n\nDATOS DEL USUARIO:\n" + data;
    }

    private void serializeStudy(StudyLogResponseDto l, StringBuilder sb) {
        if (l == null) return;
        if (l.studied()) {
            sb.append("Estudio: Sí — %.1fhs de %s\n".formatted(l.hours(), l.subject()));
        } else {
            sb.append("Estudio: No — %s\n".formatted(l.skipReason() != null ? l.skipReason() : "sin motivo"));
        }
    }

    private void serializeExercise(ExerciseLogResponseDto l, StringBuilder sb) {
        if (l == null) return;
        if (l.exercised()) {
            sb.append(String.format("Ejercicio: Sí — %.1fhs, %s, energía %d%%\n", 
                l.hours(), l.muscularGroup().toString(), l.energyLevel()));
        } else {
            sb.append("Ejercicio: No — " + (l.skipReason() != null ? l.skipReason() : "sin motivo") + "\n");
        }
    }

    private void serializeNutrition(NutritionLogResponseDto l, StringBuilder sb) {
        if (l == null) return;
        StringBuilder line = new StringBuilder("Alimentación: " + l.rating());
        line.append(l.metGoal() ? " — cumplió objetivo" : " — no cumplió objetivo");
        sb.append(line).append("\n");
    }

    private void serializeMood(MoodLogResponseDto l, StringBuilder sb) {
        if (l == null) return;
        StringBuilder line = new StringBuilder("Ánimo: " + l.mood());
        if (l.eventDescription() != null && !l.eventDescription().isEmpty()) {
            line.append(" (%s)".formatted(l.eventDescription()));
        }
        if (l.socialized()) {
            line.append(" — socializó con %s".formatted(l.socialWith()));
        }
        sb.append(line).append("\n");
    }

    private void serializeSleep(SleepLogResponseDto l, StringBuilder sb) {
        if (l == null) return;
        StringBuilder line = new StringBuilder("Sueño: %.1fhs (%s)".formatted(l.hours(), l.quality()));
        if (l.napped()) {
            line.append(" + siesta %.1fhs".formatted(l.napHours()));
        }
        sb.append(line).append("\n");
    }

    private void serializePersonal(List<com.smart.smart_backend.application.dto.habit.log.PersonalLogResponseDto> logs, StringBuilder sb) {
        if (logs == null || logs.isEmpty()) return;
        for (var l : logs) {
            String status = l.completed() ? "Completado" : "Pendiente";
            sb.append("Hábito Personal (ID %d): %s".formatted(l.habitId(), status));
            if (l.hours() != null) sb.append(" — %.1fhs".formatted(l.hours()));
            if (l.description() != null) sb.append(" (%s)".formatted(l.description()));
            sb.append("\n");
        }
    }
}