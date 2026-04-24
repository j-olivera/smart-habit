package com.smart.smart_backend.application.usecase.user;

import com.smart.smart_backend.application.dto.auth.AuthResultDto;
import com.smart.smart_backend.application.dto.auth.LoginRequestDto;
import com.smart.smart_backend.application.port.in.user.LoginUserUseCase;
import com.smart.smart_backend.application.port.out.user.JwtProviderPort;
import com.smart.smart_backend.application.port.out.user.PasswordEncoderPort;
import com.smart.smart_backend.application.port.out.user.RefreshTokenRepositoryPort;
import com.smart.smart_backend.application.port.out.user.UserRepositoryPort;
import com.smart.smart_backend.domain.model.token.RefreshToken;
import com.smart.smart_backend.domain.model.user.User;
import com.smart.smart_backend.domain.exception.InvalidCredentialsException;

import java.time.LocalDateTime;
import java.util.UUID;

public class LoginUserService implements LoginUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtProviderPort jwtProviderPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    public LoginUserService(UserRepositoryPort userRepositoryPort,
                            PasswordEncoderPort passwordEncoderPort,
                            JwtProviderPort jwtProviderPort,
                            RefreshTokenRepositoryPort refreshTokenRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtProviderPort = jwtProviderPort;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
    }

    @Override
    public AuthResultDto login(LoginRequestDto request) {
        User user = userRepositoryPort.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoderPort.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtProviderPort.generateToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        RefreshToken savedToken = refreshTokenRepositoryPort.save(refreshToken);

        return new AuthResultDto(accessToken, savedToken.getTokenHash());
    }
}
