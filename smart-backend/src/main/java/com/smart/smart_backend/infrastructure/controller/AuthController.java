package com.smart.smart_backend.infrastructure.controller;

import com.smart.smart_backend.application.dto.user.RegisterRequestDto;
import com.smart.smart_backend.application.dto.user.UserResponseDto;
import com.smart.smart_backend.application.dto.auth.AuthResultDto;
import com.smart.smart_backend.application.dto.auth.LoginRequestDto;
import com.smart.smart_backend.application.dto.auth.MessageResponseDto;
import com.smart.smart_backend.application.dto.auth.TokenResponseDto;
import com.smart.smart_backend.application.port.in.user.LoginUserUseCase;
import com.smart.smart_backend.application.port.in.user.LogoutUserUseCase;
import com.smart.smart_backend.application.port.in.user.RefreshSessionUseCase;
import com.smart.smart_backend.application.port.in.user.RegisterUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final RefreshSessionUseCase refreshSessionUseCase;
    private final LogoutUserUseCase logoutUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        UserResponseDto response = registerUserUseCase.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        AuthResultDto result = loginUserUseCase.login(request);
        ResponseCookie refreshCookie = buildRefreshCookie(result.refreshToken(), Duration.ofDays(7));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new TokenResponseDto(result.accessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AuthResultDto result = refreshSessionUseCase.refresh(refreshToken);
        ResponseCookie refreshCookie = buildRefreshCookie(result.refreshToken(), Duration.ofDays(7));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new TokenResponseDto(result.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponseDto> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            logoutUserUseCase.logout(refreshToken);
        }
        ResponseCookie clearCookie = buildRefreshCookie("", Duration.ZERO);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body(new MessageResponseDto("Logged out successfully"));
    }

    private ResponseCookie buildRefreshCookie(String value, Duration maxAge) {
        return ResponseCookie.from("refreshToken", value)
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
    }
}

