package com.smart.smart_backend.application.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
    @NotBlank(message = "Email is required")
    @Email(message = "Format must be a valid email")
    String email,

    @NotBlank(message = "Password is required")
    String password
) {}
