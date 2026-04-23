package com.smart.smart_backend.domain.model.habit;

import com.smart.smart_backend.domain.enums.HabitType;
import com.smart.smart_backend.domain.exception.GlobalException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Habit {
    private Long id;
    private Long userId; //FK
    private String name; // "Mi rutina nashi"
    private HabitType type; // enum <- STUDY | ..
    private String description; //puede ser nulo, no vacío xd
    private boolean active;
    private Instant createdAt;

    public void validate(String name, String description){
        if(name == null || name.isEmpty() || description.isEmpty()){
            throw new GlobalException("Something is wrong..");
        }
    }
}
