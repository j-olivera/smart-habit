package com.smart.smart_backend.application.dto.auth;

public record AuthResultDto(
    String accessToken,
    String refreshToken
) {}
