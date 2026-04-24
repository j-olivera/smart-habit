package com.smart.smart_backend.application.port.out.user;

import com.smart.smart_backend.domain.model.user.User;

public interface JwtProviderPort {
    String generateToken(User user);
    String extractUserEmail(String token);
    boolean isTokenValid(String token, User user);
}
