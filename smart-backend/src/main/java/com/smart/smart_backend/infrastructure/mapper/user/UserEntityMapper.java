package com.smart.smart_backend.infrastructure.mapper.user;

import com.smart.smart_backend.domain.model.user.User;
import com.smart.smart_backend.infrastructure.model.user.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {
    UserEntity toEntity(User user);

    User toDomain(UserEntity entity);
}
