package com.smart.smart_backend.application.usecase;

import com.smart.smart_backend.application.dto.auth.AuthResultDto;
import com.smart.smart_backend.application.port.in.user.RefreshSessionUseCase;
import com.smart.smart_backend.application.port.out.JwtProviderPort;
import com.smart.smart_backend.application.port.out.RefreshTokenRepositoryPort;
import com.smart.smart_backend.application.port.out.UserRepositoryPort;
import com.smart.smart_backend.domain.model.token.RefreshToken;
import com.smart.smart_backend.domain.model.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class RefreshSessionService implements RefreshSessionUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final JwtProviderPort jwtProviderPort;

    public RefreshSessionService(RefreshTokenRepositoryPort refreshTokenRepositoryPort,
                                 UserRepositoryPort userRepositoryPort,
                                 JwtProviderPort jwtProviderPort) {
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.jwtProviderPort = jwtProviderPort;
    }

    @Override
    public AuthResultDto refresh(String tokenHash) {
        RefreshToken existingToken = refreshTokenRepositoryPort.findByTokenHash(tokenHash)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (!existingToken.isValid()) {
            throw new RuntimeException("Refresh token is expired or revoked");
        }

        User user = userRepositoryPort.findById(existingToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getActive()) {
            throw new RuntimeException("User is not active");
        }

        // Revoke the old token (Token rotation)
        existingToken.revoke();
        refreshTokenRepositoryPort.save(existingToken);

        // Generate new JWT
        String accessToken = jwtProviderPort.generateToken(user);

        // Generate new RefreshToken
        RefreshToken newRefreshToken = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        RefreshToken savedToken = refreshTokenRepositoryPort.save(newRefreshToken);

        return new AuthResultDto(accessToken, savedToken.getTokenHash());
    }
}
