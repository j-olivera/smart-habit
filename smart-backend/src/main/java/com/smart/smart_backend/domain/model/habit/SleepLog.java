package com.smart.smart_backend.domain.model.habit;

import com.smart.smart_backend.domain.enums.SleepQuality;

public class SleepLog {
    private final Long id;
    private final Long entryId;
    private final Float hours;
    private final SleepQuality quality;
    private final boolean napped;
    private final Float napHours;
    private final boolean napNeeded;

    public SleepLog(Long id, Long entryId, Float hours, SleepQuality quality, boolean napped,
            Float napHours, boolean napNeeded) {
        this.id = id;
        this.entryId = entryId;
        this.hours = hours;
        this.quality = quality;
        this.napped = napped;
        this.napHours = napHours;
        this.napNeeded = napNeeded;
    }

    public static SleepLog create(Long entryId, Float hours, SleepQuality quality, boolean napped,
            Float napHours, boolean napNeeded) {
        if (hours == null || hours < 0.1 || hours > 24)
            throw new IllegalArgumentException("Hours debe estar entre 0.1 y 24");
        if (quality == null)
            throw new IllegalArgumentException("Sleep quality es obligatorio");
        if (napped) {
            if (napHours == null || napHours < 0.1 || napHours > 12)
                throw new IllegalArgumentException("Si hizo siesta, napHours debe estar entre 0.1 y 12");
        }

        return new SleepLog(null, entryId, hours, quality, napped, napHours, napNeeded);
    }

    public Long getId() {
        return id;
    }

    public Long getEntryId() {
        return entryId;
    }

    public Float getHours() {
        return hours;
    }

    public SleepQuality getQuality() {
        return quality;
    }

    public boolean isNapped() {
        return napped;
    }

    public Float getNapHours() {
        return napHours;
    }

    public boolean isNapNeeded() {
        return napNeeded;
    }

    public SleepLog update(Float hs, SleepQuality quality, boolean napped, Float napHours, boolean napNeeded) {
        SleepLog updated = SleepLog.create(
                this.entryId,
                hs,
                quality,
                napped,
                napHours,
                napNeeded);
        return new SleepLog(
                this.id,
                this.entryId,
                updated.hours,
                updated.quality,
                updated.napped,
                updated.napHours,
                updated.napNeeded);
    }
}
