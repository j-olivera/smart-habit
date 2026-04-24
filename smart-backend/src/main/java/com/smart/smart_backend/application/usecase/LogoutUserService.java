package com.smart.smart_backend.application.usecase;

import com.smart.smart_backend.application.port.in.user.LogoutUserUseCase;
import com.smart.smart_backend.application.port.out.RefreshTokenRepositoryPort;
import com.smart.smart_backend.domain.model.token.RefreshToken;

public class LogoutUserService implements LogoutUserUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    public LogoutUserService(RefreshTokenRepositoryPort refreshTokenRepositoryPort) {
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
    }

    @Override
    public void logout(String tokenHash) {
        RefreshToken token = refreshTokenRepositoryPort.findByTokenHash(tokenHash)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        token.revoke();
        refreshTokenRepositoryPort.save(token);
    }
}
