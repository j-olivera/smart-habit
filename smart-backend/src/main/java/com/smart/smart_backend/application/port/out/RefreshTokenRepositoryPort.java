package com.smart.smart_backend.application.port.out;

import com.smart.smart_backend.domain.model.token.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
