package com.smart.smart_backend.infrastructure.mapper.habit;

import com.smart.smart_backend.domain.model.habit.DailyEntry;
import com.smart.smart_backend.infrastructure.model.habit.DailyEntryEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class DailyEntryEntityMapper {

    public DailyEntry toDomain(DailyEntryEntity entity) {
        if (entity == null) return null;
        return DailyEntry.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant())
                .build();
    }

    public DailyEntryEntity toEntity(DailyEntry domain) {
        if (domain == null) return null;
        return DailyEntryEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .date(domain.getDate())
                .createdAt(domain.getCreatedAt() != null
                        ? LocalDateTime.ofInstant(domain.getCreatedAt(), ZoneId.systemDefault())
                        : LocalDateTime.now())
                .build();
    }
}
