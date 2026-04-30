package com.smart.smart_backend.domain.model.habit;

import com.smart.smart_backend.domain.enums.HabitType;
import com.smart.smart_backend.domain.exception.GlobalException;

import java.time.Instant;

public class Habit {
    private Long id;
    private Long userId; // FK
    private String name; // "Mi rutina nashi"
    private HabitType type; // enum <- STUDY | .. ? quedo redundate el enum?
    private String description; // puede ser nulo, no vacío xd
    private boolean active;
    private Instant createdAt;

    public Habit(Long id, Long userId, String name, HabitType type, String description, boolean active,
            Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static Habit create(Long userId, String name, HabitType type, String description, boolean active) {
        validate(name, description);
        return new Habit(null, userId, name, type, description, active, Instant.now());
    }

    public static void validate(String name, String description) {
        if (name == null || name.isEmpty() || description.isEmpty()) {
            throw new GlobalException("Something is wrong..");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public HabitType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
