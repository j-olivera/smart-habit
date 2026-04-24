package com.smart.smart_backend.application.mapper.logs;

import com.smart.smart_backend.application.dto.habit.log.SleepLogResponseDto;
import com.smart.smart_backend.domain.model.habit.SleepLog;

public class SleepLogMapper {
    public static SleepLogResponseDto toResponse(SleepLog sleepLog) {
        return new SleepLogResponseDto(
                sleepLog.getId(),
                sleepLog.getHabitId(),
                sleepLog.getEntryId(),
                sleepLog.getHours(),
                sleepLog.getQuality(),
                sleepLog.isNapped(),
                sleepLog.getNapHours(),
                sleepLog.isNapNeeded());
    }
}
