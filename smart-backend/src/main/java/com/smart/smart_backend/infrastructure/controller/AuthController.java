package com.smart.smart_backend.infrastructure.controller;

import com.smart.smart_backend.application.dto.RegisterRequestDto;
import com.smart.smart_backend.application.dto.UserResponseDto;
import com.smart.smart_backend.application.port.in.RegisterUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        UserResponseDto response = registerUserUseCase.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
