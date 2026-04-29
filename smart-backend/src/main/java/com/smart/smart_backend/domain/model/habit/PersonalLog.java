package com.smart.smart_backend.domain.model.habit;

public class PersonalLog {
    private final Long id;
    private final Long habitId;
    private final Long entryId;
    private final boolean completed;
    private final Float hours;
    private final String description;

    public PersonalLog(Long id, Long habitId, Long entryId, boolean completed, Float hours, String description) {
        this.id = id;
        this.habitId = habitId;
        this.entryId = entryId;
        this.completed = completed;
        this.hours = hours;
        this.description = description;
    }

    public static PersonalLog create(Long habitId, Long entryId, boolean completed, Float hours, String description) {
        if (habitId == null) {
            throw new IllegalArgumentException("Habit ID es obligatorio para un log personal");
        }
        if (entryId == null) {
            throw new IllegalArgumentException("Entry ID es obligatorio");
        }
        
        return new PersonalLog(null, habitId, entryId, completed, hours, description);
    }

    public Long getId() {
        return id;
    }

    public Long getHabitId() {
        return habitId;
    }

    public Long getEntryId() {
        return entryId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Float getHours() {
        return hours;
    }

    public String getDescription() {
        return description;
    }

    public PersonalLog update(boolean completed, Float hours, String description) {
        PersonalLog updated = PersonalLog.create(
                this.habitId,
                this.entryId,
                completed,
                hours,
                description
        );
        return new PersonalLog(
                this.id,
                this.habitId,
                this.entryId,
                updated.completed,
                updated.hours,
                updated.description
        );
    }
}
/*
 mirar el funcionamiento de esta clase en /docs/registros
 */