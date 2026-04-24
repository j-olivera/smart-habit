package com.smart.smart_backend.application.port.in.habit;

public interface DesactivateHabit {
    void execute(Long habitId, Long userId);
}
