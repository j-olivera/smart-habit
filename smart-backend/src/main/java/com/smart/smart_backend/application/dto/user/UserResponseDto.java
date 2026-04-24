package com.smart.smart_backend.application.dto.user;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UserResponseDto(
        Long id,
        String name,
        String email,
        String role,
        LocalDateTime createdAt,
        Boolean active) {
}
