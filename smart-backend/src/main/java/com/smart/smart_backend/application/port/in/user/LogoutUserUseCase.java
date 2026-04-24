package com.smart.smart_backend.application.port.in.user;

public interface LogoutUserUseCase {
    void logout(String tokenHash);
}
