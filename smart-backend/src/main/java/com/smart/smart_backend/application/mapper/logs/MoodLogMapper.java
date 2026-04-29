package com.smart.smart_backend.application.mapper.logs;

import com.smart.smart_backend.application.dto.habit.log.MoodLogResponseDto;
import com.smart.smart_backend.domain.model.habit.MoodLog;

public class MoodLogMapper {
    public static MoodLogResponseDto toResponse(MoodLog moodLog) {
        return new MoodLogResponseDto(
                moodLog.getId(),

                moodLog.getEntryId(),
                moodLog.getMood(),
                moodLog.isHasObservations(),
                moodLog.getEventDescription(),
                moodLog.isSocialized(),
                moodLog.getSocialWith());
    }
}
