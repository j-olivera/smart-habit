package com.smart.smart_backend.infrastructure.adapter;

import com.smart.smart_backend.application.port.out.user.RefreshTokenRepositoryPort;
import com.smart.smart_backend.domain.model.token.RefreshToken;
import com.smart.smart_backend.infrastructure.mapper.RefreshTokenEntityMapper;
import com.smart.smart_backend.infrastructure.model.RefreshTokenEntity;
import com.smart.smart_backend.infrastructure.repository.JpaRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final JpaRefreshTokenRepository jpaRepository;
    private final RefreshTokenEntityMapper mapper;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        if (refreshToken.getCreatedAt() == null) {
            refreshToken.setCreatedAt(LocalDateTime.now());
        }
        RefreshTokenEntity entity = mapper.toEntity(refreshToken);
        RefreshTokenEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash)
                .map(mapper::toDomain);
    }
}
