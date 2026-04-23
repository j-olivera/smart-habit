package com.smart.smart_backend.domain.model.habit;

import com.smart.smart_backend.domain.enums.MuscularGroup;
import com.smart.smart_backend.domain.exception.InvalidHoursException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class ExerciseLog {
    private Long id;
    private Long habitId; //FK
    private Long entryId; // FK
    private boolean exercised; //
    private Float hours; // nulo si exercised es falso
    private MuscularGroup muscularGroup; // x2
    private Integer energyLevel; // 1 - 100 | x3
    private String skipReason;// nulo si exercised es verdadero
    //validaciones de horas
    public void validate(Float hours){
        if(hours<0.1 || hours > 4){ // // se puede entrenar mas de 4 horas pero PERO
            throw new InvalidHoursException("That time is not possible");
        }
    }
}
