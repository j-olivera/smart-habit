package com.smart.smart_backend.domain.model.habit;

import com.smart.smart_backend.domain.exception.InvalidHoursException;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudyLog {
    private Long id;
    private Long habitId; // FK
    private boolean studied;
    private Float hours; // nulo si studied is false
    private String subject;// nulo si studied is false
    private String skipReason; // nulo si studied is false
    // validaciones de horas

    public void validate(Float hours) {
        if (hours != null && hours < 0.1 || hours != null && hours > 12) { // 0.1 equivaldría a 10' ? sería un caso feo
                                                                           // pero posible
            throw new InvalidHoursException("That time is not possible");
        }
    }
}

/*
 * Los hábitos de log ya no son value objects embebidos en el agregado
 * — son entidades relacionadas que conocen tanto al Habit (definición) como al
 * DailyEntry (día).
 */