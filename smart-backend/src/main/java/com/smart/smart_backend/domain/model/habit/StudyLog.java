package com.smart.smart_backend.domain.model.habit;

public class StudyLog {
    private final Long id;
    private final Long entryId;
    private final boolean studied;
    private final Float hours;
    private final String subject;
    private final String skipReason; // nulo si studied is false

    public StudyLog(Long id, Long entryId, boolean studied, Float hours, String subject,
            String skipReason) {
        this.id = id;
        this.entryId = entryId;
        this.studied = studied;
        this.hours = hours;
        this.subject = subject;
        this.skipReason = skipReason;
    }

    public static StudyLog create(Long entryId,
            boolean studied, Float hours,
            String subject, String skipReason) {
        if (studied) {
            if (hours == null || hours < 0.1 || hours > 12)
                throw new IllegalArgumentException(
                        "Si estudió, hours debe estar entre 0 y 12");
            if (subject == null || subject.isBlank())
                throw new IllegalArgumentException(
                        "Si estudió, subject es obligatorio");
        } else {
            if (skipReason == null || skipReason.isBlank())
                throw new IllegalArgumentException(
                        "Si no estudió, skipReason es obligatorio");
        }
        return new StudyLog(null, entryId,
                studied, hours, subject, skipReason);
    }

    public Long getId() {
        return id;
    }

    public Long getEntryId() {
        return entryId;
    }

    public boolean isStudied() {
        return studied;
    }

    public Float getHours() {
        return hours;
    }

    public String getSubject() {
        return subject;
    }

    public String getSkipReason() {
        return skipReason;
    }

    // cambio
    public StudyLog update(boolean stud, Float hs, String subj, String skipReason) {
        StudyLog updated = StudyLog.create(
                this.entryId,
                stud,
                hs,
                subj,
                skipReason);
        return new StudyLog(
                this.id, // conserva el id actual
                this.entryId,
                updated.studied,
                updated.hours,
                updated.subject,
                updated.skipReason);
    }
}

/*
 * Los hábitos de log ya no son value objects embebidos en el agregado
 * — son entidades relacionadas que conocen tanto al Habit (definición) como al
 * DailyEntry (día).
 */
