package com.smart.smart_backend.infrastructure.mapper.habit;

import com.smart.smart_backend.domain.model.habit.StudyLog;
import com.smart.smart_backend.infrastructure.model.habit.StudyLogEntity;
import org.springframework.stereotype.Component;

@Component
public class StudyLogEntityMapper {

    public StudyLog toDomain(StudyLogEntity entity) {
        if (entity == null) return null;
        return new StudyLog(
                entity.getId(),

                entity.getEntryId(),
                entity.getStudied(),
                entity.getHours(),
                entity.getSubject(),
                entity.getSkipReason()
        );
    }

    public StudyLogEntity toEntity(StudyLog domain) {
        if (domain == null) return null;
        return StudyLogEntity.builder()
                .id(domain.getId())

                .entryId(domain.getEntryId())
                .studied(domain.isStudied())
                .hours(domain.getHours())
                .subject(domain.getSubject())
                .skipReason(domain.getSkipReason())
                .build();
    }
}
