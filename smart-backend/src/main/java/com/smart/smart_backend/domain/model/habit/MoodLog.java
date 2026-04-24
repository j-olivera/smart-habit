package com.smart.smart_backend.domain.model.habit;

import com.smart.smart_backend.domain.enums.MoodLevel;

public class MoodLog {
    private final Long id;
    private final Long habitId;
    private final Long entryId;
    private final MoodLevel mood;
    private final boolean hasObservations;
    private final String eventDescription;
    private final boolean socialized;
    private final String socialWith;

    public MoodLog(Long id, Long habitId, Long entryId, MoodLevel mood, boolean hasObservations,
            String eventDescription, boolean socialized, String socialWith) {
        this.id = id;
        this.habitId = habitId;
        this.entryId = entryId;
        this.mood = mood;
        this.hasObservations = hasObservations;
        this.eventDescription = eventDescription;
        this.socialized = socialized;
        this.socialWith = socialWith;
    }

    public static MoodLog create(Long habitId, Long entryId, MoodLevel mood, boolean hasObservations,
            String eventDescription, boolean socialized, String socialWith) {
        if (mood == null)
            throw new IllegalArgumentException("Mood level es obligatorio");
        if (hasObservations && (eventDescription == null || eventDescription.isBlank()))
            throw new IllegalArgumentException("Si hay observaciones, eventDescription es obligatorio");
        if (socialized && (socialWith == null || socialWith.isBlank()))
            throw new IllegalArgumentException("Si socializó, socialWith es obligatorio");

        return new MoodLog(null, habitId, entryId, mood, hasObservations, eventDescription, socialized, socialWith);
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

    public MoodLevel getMood() {
        return mood;
    }

    public boolean isHasObservations() {
        return hasObservations;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public boolean isSocialized() {
        return socialized;
    }

    public String getSocialWith() {
        return socialWith;
    }

    public MoodLog update(MoodLevel mood, boolean hasObs, String eventDesc, boolean socialized, String socialWith) {
        MoodLog updated = MoodLog.create(
                this.habitId,
                this.entryId,
                mood,
                hasObs,
                eventDesc,
                socialized,
                socialWith);
        return new MoodLog(
                this.id,
                this.habitId,
                this.entryId,
                updated.mood,
                updated.hasObservations,
                updated.eventDescription,
                updated.socialized,
                updated.socialWith);
    }
}
