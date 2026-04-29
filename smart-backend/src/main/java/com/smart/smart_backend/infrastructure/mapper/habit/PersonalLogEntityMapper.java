package com.smart.smart_backend.infrastructure.mapper.habit;

import com.smart.smart_backend.domain.model.habit.PersonalLog;
import com.smart.smart_backend.infrastructure.model.habit.PersonalLogEntity;
import org.springframework.stereotype.Component;

@Component
public class PersonalLogEntityMapper {

    public PersonalLog toDomain(PersonalLogEntity entity) {
        if (entity == null) return null;
        return new PersonalLog(
                entity.getId(),
                entity.getHabitId(),
                entity.getEntryId(),
                entity.getCompleted(),
                entity.getHours(),
                entity.getDescription()
        );
    }

    public PersonalLogEntity toEntity(PersonalLog domain) {
        if (domain == null) return null;
        return PersonalLogEntity.builder()
                .id(domain.getId())
                .habitId(domain.getHabitId())
                .entryId(domain.getEntryId())
                .completed(domain.isCompleted())
                .hours(domain.getHours())
                .description(domain.getDescription())
                .build();
    }
}
