package com.smart.smart_backend.application.port.in;

import com.smart.smart_backend.application.dto.auth.AuthResultDto;
import com.smart.smart_backend.application.dto.auth.LoginRequestDto;

public interface LoginUserUseCase {
    AuthResultDto login(LoginRequestDto request);
}
