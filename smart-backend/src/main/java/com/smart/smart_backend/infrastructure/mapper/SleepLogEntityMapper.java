package com.smart.smart_backend.infrastructure.mapper;

import com.smart.smart_backend.domain.enums.SleepQuality;
import com.smart.smart_backend.domain.model.habit.SleepLog;
import com.smart.smart_backend.infrastructure.model.SleepLogEntity;
import org.springframework.stereotype.Component;

@Component
public class SleepLogEntityMapper {

    public SleepLog toDomain(SleepLogEntity entity) {
        if (entity == null) return null;
        return new SleepLog(
                entity.getId(),
                entity.getHabitId(),
                entity.getEntryId(),
                entity.getHours(),
                SleepQuality.valueOf(entity.getQuality()),
                entity.getNapped(),
                entity.getNapHours(),
                entity.getNapNeeded()
        );
    }

    public SleepLogEntity toEntity(SleepLog domain) {
        if (domain == null) return null;
        return SleepLogEntity.builder()
                .id(domain.getId())
                .habitId(domain.getHabitId())
                .entryId(domain.getEntryId())
                .hours(domain.getHours())
                .quality(domain.getQuality().name())
                .napped(domain.isNapped())
                .napHours(domain.getNapHours())
                .napNeeded(domain.isNapNeeded())
                .build();
    }
}
