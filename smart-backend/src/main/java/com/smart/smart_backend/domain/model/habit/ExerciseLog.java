package com.smart.smart_backend.domain.model.habit;

import com.smart.smart_backend.domain.enums.MuscularGroup;

public class ExerciseLog {
    private final Long id;
    private final Long entryId;
    private final boolean exercised;
    private final Float hours;
    private final MuscularGroup muscularGroup;
    private final Integer energyLevel;
    private final String skipReason;

    public ExerciseLog(Long id, Long entryId, boolean exercised, Float hours,
            MuscularGroup muscularGroup, Integer energyLevel, String skipReason) {
        this.id = id;
        this.entryId = entryId;
        this.exercised = exercised;
        this.hours = hours;
        this.muscularGroup = muscularGroup;
        this.energyLevel = energyLevel;
        this.skipReason = skipReason;
    }

    public static ExerciseLog create(Long entryId, boolean exercised, Float hours,
            MuscularGroup muscularGroup, Integer energyLevel, String skipReason) {
        if (exercised) {
            if (hours == null || hours < 0.1 || hours > 4)
                throw new IllegalArgumentException("Si entrenó, hours debe estar entre 0.1 y 12");
            if (muscularGroup == null)
                throw new IllegalArgumentException("Si entrenó, muscularGroup es obligatorio");
            if (energyLevel == null || energyLevel < 1 || energyLevel > 100)
                throw new IllegalArgumentException("Si entrenó, energyLevel debe estar entre 1 y 100");
        } else {
            if (skipReason == null || skipReason.isBlank())
                throw new IllegalArgumentException("Si no entrenó, skipReason es obligatorio");
        }
        return new ExerciseLog(null, entryId, exercised, hours, muscularGroup, energyLevel, skipReason);
    }

    public Long getId() {
        return id;
    }

    public Long getEntryId() {
        return entryId;
    }

    public boolean isExercised() {
        return exercised;
    }

    public Float getHours() {
        return hours;
    }

    public MuscularGroup getMuscularGroup() {
        return muscularGroup;
    }

    public Integer getEnergyLevel() {
        return energyLevel;
    }

    public String getSkipReason() {
        return skipReason;
    }

    public ExerciseLog update(boolean exerc, Float hs, MuscularGroup mg, Integer energy, String skipReason) {
        ExerciseLog updated = ExerciseLog.create(
                this.entryId,
                exerc,
                hs,
                mg,
                energy,
                skipReason);
        return new ExerciseLog(
                this.id,
                this.entryId,
                updated.exercised,
                updated.hours,
                updated.muscularGroup,
                updated.energyLevel,
                updated.skipReason);
    }
}
