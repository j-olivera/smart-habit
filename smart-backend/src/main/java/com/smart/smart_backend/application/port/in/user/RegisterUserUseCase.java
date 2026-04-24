package com.smart.smart_backend.application.port.in.user;

import com.smart.smart_backend.application.dto.user.RegisterRequestDto;
import com.smart.smart_backend.application.dto.user.UserResponseDto;

public interface RegisterUserUseCase {
    UserResponseDto register(RegisterRequestDto request);
}
