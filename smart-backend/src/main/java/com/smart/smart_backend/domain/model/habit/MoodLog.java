package com.smart.smart_backend.domain.model.habit;

import com.smart.smart_backend.domain.enums.MoodLevel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoodLog {
    private Long id;
    private Long habitId; //FK
    private Long entryId; // FK
    private MoodLevel mood;
    private boolean hasObservations;
    private String eventDescription; // nulo si hasObservations is false
    private boolean socialized;
    private String socialWith; // nulo si socialized is false
}
