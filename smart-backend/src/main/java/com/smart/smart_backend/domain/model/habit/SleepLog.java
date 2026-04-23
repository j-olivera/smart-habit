package com.smart.smart_backend.domain.model.habit;

import com.smart.smart_backend.domain.enums.SleepQuality;
import com.smart.smart_backend.domain.exception.InvalidHoursException;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SleepLog {
    private Long id;
    private Long habitId;
    private Long entryId;
    private Float hours; // no puede ser nulo, en teoría
    private SleepQuality quality;
    private boolean napped;
    private Float napHours; // null si napped es falso
    private boolean napNeeded;  // x2

    //validaciones de horas
    public void validate(Float hours, Float napHours){
        if(hours!=null && hours<0.1 || hours != null && hours > 12){ //
            throw new InvalidHoursException("That time is not possible");
        }
        if(napHours<0.1 || napHours > 4){
            throw new InvalidHoursException("That time is not possible");
        }
    }
}
