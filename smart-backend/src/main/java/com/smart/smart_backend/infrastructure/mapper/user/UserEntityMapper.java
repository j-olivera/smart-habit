package com.smart.smart_backend.infrastructure.mapper.user;

import com.smart.smart_backend.domain.model.user.User;
import com.smart.smart_backend.infrastructure.model.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {

    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        return UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .active(user.getActive())
                .build();
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .role(entity.getRole())
                .createdAt(entity.getCreatedAt())
                .active(entity.getActive())
                .build();
    }
}