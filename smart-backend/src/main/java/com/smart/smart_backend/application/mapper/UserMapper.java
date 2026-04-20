package com.smart.smart_backend.application.mapper;

import com.smart.smart_backend.application.dto.UserResponseDto;
import com.smart.smart_backend.domain.User;

public class UserMapper {
    public static UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getActive());
    }
}
