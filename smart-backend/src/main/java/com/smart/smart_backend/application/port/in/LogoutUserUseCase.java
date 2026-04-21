package com.smart.smart_backend.application.port.in;

public interface LogoutUserUseCase {
    void logout(String tokenHash);
}
