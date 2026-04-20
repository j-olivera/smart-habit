package com.smart.smart_backend.infrastructure.mapper;

import com.smart.smart_backend.domain.User;
import com.smart.smart_backend.infrastructure.model.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {
    UserEntity toEntity(User user);
    User toDomain(UserEntity entity);
}
