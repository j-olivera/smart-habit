package com.smart.smart_backend.application.port.in;

import com.smart.smart_backend.application.dto.auth.AuthResultDto;

public interface RefreshSessionUseCase {
    AuthResultDto refresh(String tokenHash);
}
