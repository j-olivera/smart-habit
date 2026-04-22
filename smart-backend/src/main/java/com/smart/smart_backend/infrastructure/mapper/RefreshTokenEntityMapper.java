package com.smart.smart_backend.infrastructure.mapper;

import com.smart.smart_backend.domain.model.token.RefreshToken;
import com.smart.smart_backend.infrastructure.model.RefreshTokenEntity;
import com.smart.smart_backend.infrastructure.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefreshTokenEntityMapper {
    
    @Mapping(target = "userId", source = "user.id")
    RefreshToken toDomain(RefreshTokenEntity entity);

    @Mapping(target = "user", expression = "java(mapUserIdToUserEntity(domain.getUserId()))")
    RefreshTokenEntity toEntity(RefreshToken domain);

    default UserEntity mapUserIdToUserEntity(Long userId) {
        if (userId == null) {
            return null;
        }
        UserEntity user = new UserEntity();
        user.setId(userId);
        return user;
    }
}
