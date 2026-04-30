package com.smart.smart_backend.application.mapper.logs;

import com.smart.smart_backend.application.dto.habit.log.PersonalLogResponseDto;
import com.smart.smart_backend.domain.model.habit.PersonalLog;

public class PersonalLogMapper {
    public static PersonalLogResponseDto toResponse(PersonalLog personalLog, String habitN) {
        return new PersonalLogResponseDto(
                personalLog.getId(),
                personalLog.getHabitId(),
                personalLog.getEntryId(),
                habitN,
                personalLog.isCompleted(),
                personalLog.getHours(),
                personalLog.getDescription());
    }
}
