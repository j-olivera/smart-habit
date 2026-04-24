package com.smart.smart_backend.application.dto.habit;

import com.smart.smart_backend.domain.enums.HabitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record HabitRequestDto(
    @NotBlank(message = "Name cannot be empty")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    String name,

    @NotNull(message = "Habit type is required")
    HabitType type,

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    String description
) {}
