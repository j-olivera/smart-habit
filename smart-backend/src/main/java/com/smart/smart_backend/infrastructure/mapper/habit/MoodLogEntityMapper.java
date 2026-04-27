package com.smart.smart_backend.infrastructure.mapper.habit;

import com.smart.smart_backend.domain.enums.MoodLevel;
import com.smart.smart_backend.domain.model.habit.MoodLog;
import com.smart.smart_backend.infrastructure.model.habit.MoodLogEntity;
import org.springframework.stereotype.Component;

@Component
public class MoodLogEntityMapper {

    public MoodLog toDomain(MoodLogEntity entity) {
        if (entity == null) return null;
        return new MoodLog(
                entity.getId(),
                entity.getHabitId(),
                entity.getEntryId(),
                MoodLevel.valueOf(entity.getMood()),
                entity.getHasObservations(),
                entity.getEventDescription(),
                entity.getSocialized(),
                entity.getSocialWith()
        );
    }

    public MoodLogEntity toEntity(MoodLog domain) {
        if (domain == null) return null;
        return MoodLogEntity.builder()
                .id(domain.getId())
                .habitId(domain.getHabitId())
                .entryId(domain.getEntryId())
                .mood(domain.getMood().name())
                .hasObservations(domain.isHasObservations())
                .eventDescription(domain.getEventDescription())
                .socialized(domain.isSocialized())
                .socialWith(domain.getSocialWith())
                .build();
    }
}
