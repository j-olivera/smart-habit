package com.smart.smart_backend.application.port.in;

import com.smart.smart_backend.application.dto.RegisterRequestDto;
import com.smart.smart_backend.application.dto.UserResponseDto;

public interface RegisterUserUseCase {
    UserResponseDto register(RegisterRequestDto request);
}
