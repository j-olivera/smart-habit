package com.smart.smart_backend.infrastructure.model.habit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "habit_mood")
public class MoodLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entry_id", nullable = false)
    private Long entryId;


    @Column(nullable = false)
    private String mood;

    @Column(name = "has_observations", nullable = false)
    private Boolean hasObservations;

    @Column(name = "event_description")
    private String eventDescription;

    @Column(nullable = false)
    private Boolean socialized;

    @Column(name = "social_with")
    private String socialWith;
}
