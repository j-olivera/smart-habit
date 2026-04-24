package com.smart.smart_backend.application.dto.user;

public record UserRegisterCommand(
        String name,
        String email,
        String password
){}
