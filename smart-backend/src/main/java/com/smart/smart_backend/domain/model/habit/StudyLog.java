package com.smart.smart_backend.domain.model.habit;

import com.smart.smart_backend.domain.exception.InvalidHoursException;
import lombok.Builder;
import lombok.Data;


public class StudyLog {
    private final Long id;
    private final Long habitId;
    private final Long entryId;
    private final boolean studied;
    private final Integer hours;
    private final String subject;
    private final String skipReason; // nulo si studied is false

    public StudyLog(Long id, Long habitId, Long entryId, boolean studied, Integer hours, String subject, String skipReason) {
        this.id = id;
        this.habitId = habitId;
        this.entryId = entryId;
        this.studied = studied;
        this.hours = hours;
        this.subject = subject;
        this.skipReason = skipReason;
    }

    public static StudyLog create(Long habitId, Long entryId,
                                  boolean studied, Integer hours,
                                  String subject, String skipReason) {
        if (studied) {
            if (hours == null || hours < 0 || hours > 24)
                throw new IllegalArgumentException(
                        "Si estudió, hours debe estar entre 0 y 24");
            if (subject == null || subject.isBlank())
                throw new IllegalArgumentException(
                        "Si estudió, subject es obligatorio");
        } else {
            if (skipReason == null || skipReason.isBlank())
                throw new IllegalArgumentException(
                        "Si no estudió, skipReason es obligatorio");
        }
        return new StudyLog(null, habitId, entryId,
                studied, hours, subject, skipReason);
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

    public boolean isStudied() {
        return studied;
    }

    public Integer getHours() {
        return hours;
    }

    public String getSubject() {
        return subject;
    }

    public String getSkipReason() {
        return skipReason;
    }
}

/*
 * Los hábitos de log ya no son value objects embebidos en el agregado
 * — son entidades relacionadas que conocen tanto al Habit (definición) como al
 * DailyEntry (día).
 */