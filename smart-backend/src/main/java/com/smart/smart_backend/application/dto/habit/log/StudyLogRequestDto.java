package com.smart.smart_backend.application.dto.habit.log;

public record StudyLogRequestDto(
        Long habitId,
        Long entryId,
        boolean studied,
        Float hours,
        String subject,
        String skipReason) {
}
