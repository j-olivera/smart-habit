package com.smart.smart_backend.domain.model.habit;

import com.smart.smart_backend.domain.enums.NutritionRating;

public class NutritionLog {
    private final Long id;
    private final Long habitId;
    private final Long entryId;
    private final NutritionRating rating;
    private final boolean hasObservation;
    private final boolean metGoal;

    public NutritionLog(Long id, Long habitId, Long entryId, NutritionRating rating, boolean hasObservation,
            boolean metGoal) {
        this.id = id;
        this.habitId = habitId;
        this.entryId = entryId;
        this.rating = rating;
        this.hasObservation = hasObservation;
        this.metGoal = metGoal;
    }

    public static NutritionLog create(Long habitId, Long entryId, NutritionRating rating, boolean hasObservation,
            boolean metGoal) {
        if (rating == null)
            throw new IllegalArgumentException("Nutrition rating es obligatorio");

        return new NutritionLog(null, habitId, entryId, rating, hasObservation, metGoal);
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

    public NutritionRating getRating() {
        return rating;
    }

    public boolean isHasObservation() {
        return hasObservation;
    }

    public boolean isMetGoal() {
        return metGoal;
    }
}
