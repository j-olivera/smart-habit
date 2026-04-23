package com.smart.smart_backend.application.mapper;

import com.smart.smart_backend.application.dto.UserResponseDto;
import com.smart.smart_backend.domain.model.user.User;

public class UserMapper {
    public static UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .active(user.getActive())
                .build();
    }
}
