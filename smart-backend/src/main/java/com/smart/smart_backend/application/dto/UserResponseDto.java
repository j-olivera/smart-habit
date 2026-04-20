package com.smart.smart_backend.application.dto;

import java.time.LocalDateTime;

public record UserResponseDto(
    Long id,
    String name,
    String email,
    String role,
    LocalDateTime createdAt,
    Boolean active
) {}
