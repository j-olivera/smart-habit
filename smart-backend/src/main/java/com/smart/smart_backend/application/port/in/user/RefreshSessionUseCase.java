package com.smart.smart_backend.application.port.in.user;

import com.smart.smart_backend.application.dto.auth.AuthResultDto;

public interface RefreshSessionUseCase {
    AuthResultDto refresh(String tokenHash);
}
