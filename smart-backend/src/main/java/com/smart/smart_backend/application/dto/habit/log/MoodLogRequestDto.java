package com.smart.smart_backend.application.dto.habit.log;

import com.smart.smart_backend.domain.enums.MoodLevel;
import jakarta.validation.constraints.NotNull;

public record MoodLogRequestDto(

    @NotNull(message = "Entry ID is required")
    Long entryId,
    
    @NotNull(message = "Mood level is required")
    MoodLevel mood,
    
    boolean hasObservations,
    String eventDescription,
    boolean socialized,
    String socialWith
) {}
