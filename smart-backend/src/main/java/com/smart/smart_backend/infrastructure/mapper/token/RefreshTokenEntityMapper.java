package com.smart.smart_backend.infrastructure.mapper.token;

import com.smart.smart_backend.domain.model.token.RefreshToken;
import com.smart.smart_backend.infrastructure.model.token.RefreshTokenEntity;
import com.smart.smart_backend.infrastructure.model.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenEntityMapper {

    public RefreshToken toDomain(RefreshTokenEntity entity) {
        if (entity == null) {
            return null;
        }

        Long userId = null;
        if (entity.getUser() != null) {
            userId = entity.getUser().getId();
        }

        return RefreshToken.builder()
                .id(entity.getId())
                .userId(userId)
                .tokenHash(entity.getTokenHash())
                .expiresAt(entity.getExpiresAt())
                .revoked(entity.getRevoked())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public RefreshTokenEntity toEntity(RefreshToken domain) {
        if (domain == null) {
            return null;
        }

        UserEntity user = null;
        if (domain.getUserId() != null) {
            user = new UserEntity();
            user.setId(domain.getUserId());
        }

        return RefreshTokenEntity.builder()
                .id(domain.getId())
                .user(user)
                .tokenHash(domain.getTokenHash())
                .expiresAt(domain.getExpiresAt())
                .revoked(domain.getRevoked())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}