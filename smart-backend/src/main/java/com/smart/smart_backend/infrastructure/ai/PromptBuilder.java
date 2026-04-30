package com.smart.smart_backend.infrastructure.ai;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;
import com.smart.smart_backend.application.dto.habit.log.ExerciseLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.MoodLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.NutritionLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.PersonalLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.SleepLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.StudyLogResponseDto;
import com.smart.smart_backend.application.port.out.ai.PromptBuilderPort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class PromptBuilder implements PromptBuilderPort {

    private static final String SYSTEM_PROMPT = """
                Eres un coach de hábitos directo, honesto y emocionalmente inteligente.
                Tu rol es analizar los registros semanales de un usuario y generar un reporte
                útil, realista y humano.

                PRINCIPIOS QUE DEFINEN TU VOZ:
                - Hablás en segunda persona, de forma cercana pero sin exagerar ("lograste", "te costó").
                - No adulás ni minimizás. Si la semana fue mala, lo decís con respeto.
                - Usás los datos concretos para respaldar cada observación. Nada inventado.
                - Si el usuario dejó una razón de por qué no hizo algo, la tenés en cuenta
                como contexto real — no la ignorás ni la juzgás.
                - El estado de ánimo registrado es información emocional valiosa:
                integrala en el análisis cuando sea relevante.
                - Si un hábito no aparece registrado un día, NO asumas que no se realizó.
                - No uses lenguaje clínico, alarmista ni corporativo.
                - Nunca uses frases vacías como "¡Excelente semana!", "¡Sigue así!" sin sustento.

                ESTRUCTURA DEL REPORTE (respetá los títulos exactos):

                ## RESUMEN DE LA SEMANA
                2 o 3 oraciones honestas sobre cómo fue la semana en términos generales.
                Mencioná el patrón dominante: ¿fue constante, irregular, difícil, sólida?
                Si el ánimo fue bajo varios días, reconocelo — forma parte del contexto.

                ## LO QUE FUNCIONÓ
                Máximo 3 puntos. Solo lo que realmente está respaldado por los datos.
                Sé específico: no "hiciste ejercicio" sino "entrenaste 3 de 5 días con
                energía alta, enfocándote en piernas y espalda".
                Si no hay nada destacable, decilo honestamente con una sola línea.

                ## DÓNDE HUBO DIFICULTADES
                Máximo 3 puntos. Basados en ausencias, skipReasons, niveles bajos o
                patrones inconsistentes. No es una crítica — es un diagnóstico.
                Si el usuario explicó por qué no hizo algo, usá esa razón como contexto,
                no como excusa ni como falla.

                ## PATRONES A PRESTAR ATENCIÓN
                Solo si existen patrones reales: poco sueño sostenido, ánimo bajo
                correlacionado con baja actividad, días sin registro agrupados, etc.
                Si no hay patrones preocupantes, escribí exactamente:
                "Sin patrones de riesgo detectados esta semana."
                No inventes riesgos.

                ## PARA LA PRÓXIMA SEMANA
                Máximo 3 sugerencias concretas y accionables, derivadas directamente
                de los datos. No consejos genéricos de wellness.
                Ejemplo útil: "Los días que no estudiaste mencionaste cansancio —
                considerá mover el bloque de estudio a la mañana."
                Ejemplo inútil: "Recordá mantener una rutina constante."

                ## UN PENSAMIENTO FINAL
                1 o 2 oraciones de aliento honestas, en contexto de cómo fue
                realmente la semana. Si fue dura, reconocelo y motivá desde ahí.
                Si fue buena, celebralo con sustento. Nunca genérico.

                RESPONDE SIEMPRE EN ESPAÑOL.
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
        if (l == null)
            return;
        if (l.studied()) {
            sb.append("Estudio: Sí — %.1fhs de %s\n".formatted(l.hours(), l.subject()));
        } else {
            sb.append("Estudio: No — %s\n".formatted(l.skipReason() != null ? l.skipReason() : "sin motivo"));
        }
    }

    private void serializeExercise(ExerciseLogResponseDto l, StringBuilder sb) {
        if (l == null)
            return;
        if (l.exercised()) {
            sb.append(String.format("Ejercicio: Sí — %.1fhs, %s, energía %d%%\n",
                    l.hours(), l.muscularGroup().toString(), l.energyLevel()));
        } else {
            sb.append("Ejercicio: No — " + (l.skipReason() != null ? l.skipReason() : "sin motivo") + "\n");
        }
    }

    private void serializeNutrition(NutritionLogResponseDto l, StringBuilder sb) {
        if (l == null)
            return;
        sb.append("Alimentación: ").append(traducirNutrition(l.rating().name()));
        if (l.hasObservation() && l.metGoal() == true) {
            sb.append(l.metGoal() ? " — cumplió su objetivo nutricional"
                    : " — no cumplió su objetivo nutricional");
        }
        sb.append("\n");
    }

    private void serializeMood(MoodLogResponseDto l, StringBuilder sb) {
        if (l == null)
            return;
        sb.append("Ánimo: ").append(traducirMood(l.mood().name()));
        if (l.eventDescription() != null && !l.eventDescription().isBlank())
            sb.append(" — motivo: \"%s\"".formatted(l.eventDescription()));
        if (l.socialized()) {
            String con = l.socialWith() != null ? " con " + l.socialWith() : "";
            sb.append(" | Socializó%s".formatted(con));
        } else {
            sb.append(" | No socializó");
        }
        sb.append("\n");
    }

    private void serializeSleep(SleepLogResponseDto l, StringBuilder sb) {
        if (l == null)
            return;
        StringBuilder line = new StringBuilder(
                "Sueño: %.1fhs (%s)".formatted(l.hours(), traducirSleepQuality(l.quality().name())));
        if (l.napped()) {
            line.append(" + siesta %.1fhs".formatted(l.napHours()));
        }
        sb.append(line).append("\n");
    }

    private void serializePersonal(List<PersonalLogResponseDto> logs, StringBuilder sb) {
        if (logs == null || logs.isEmpty())
            return;
        for (var l : logs) {
            String nombre = l.habitName() != null ? l.habitName() : "Hábito personal";
            String estado = l.completed() ? "Completado" : "No completado";
            sb.append("%s: %s".formatted(nombre, estado));
            if (l.hours() != null)
                sb.append(" — %.1fhs".formatted(l.hours()));
            if (l.description() != null && !l.description().isBlank())
                sb.append(" (%s)".formatted(l.description()));
            sb.append("\n");
        }
    }

    // El modelo entiende mejor español natural que los enums en inglés
    private String traducirMood(String mood) {
        return switch (mood) {
            case "SAD" -> "Triste";
            case "DOWN" -> "Decaído";
            case "NEUTRAL" -> "Neutro";
            case "HAPPY" -> "Feliz";
            case "EUPHORIC" -> "Eufórico";
            default -> mood;
        };
    }

    private String traducirSleepQuality(String q) {
        return switch (q) {
            case "BAD" -> "Mala";
            case "REGULAR" -> "Regular";
            case "GOOD" -> "Buena";
            case "EXCELLENT" -> "Excelente";
            default -> q;
        };
    }

    private String traducirNutrition(String r) {
        return switch (r) {
            case "POOR" -> "Mala";
            case "REGULAR" -> "Regular";
            case "GOOD" -> "Buena";
            case "EXCELLENT" -> "Excelente";
            default -> r;
        };
    }

}

/*
 * Al agente le deben llegar los datos de la siguiente forma
 * --- 2026-04-21 ---
 * Estudio: Sí — 2.0hs de Java Hexagonal Architecture
 * Ejercicio: No — "tenía mucho dolor de espalda"
 * Alimentación: Buena — cumplió su objetivo nutricional
 * Ánimo: Decaído — motivo: "mal día en el trabajo" | No socializó
 * Sueño: 6.0hs (Regular) + siesta 1.0hs
 * Crossfit: Completado — 1.0hs (entrenamiento matutino)
 * 
 * --- 2026-04-22 ---
 * Estudio: No — "estaba muy cansado"
 * Ejercicio: Sí — 1.5hs, Piernas, energía 75%
 * Alimentación: Regular
 * Ánimo: Neutro | Socializó con amigos del trabajo
 * Sueño: 7.5hs (Buena)
 */