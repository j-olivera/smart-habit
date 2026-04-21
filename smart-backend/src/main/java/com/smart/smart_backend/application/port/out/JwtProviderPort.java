package com.smart.smart_backend.application.port.out;

import com.smart.smart_backend.domain.User;

public interface JwtProviderPort {
    String generateToken(User user);
    String extractUserEmail(String token);
    boolean isTokenValid(String token, User user);
}
