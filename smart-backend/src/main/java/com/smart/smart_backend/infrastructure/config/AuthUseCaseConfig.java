package com.smart.smart_backend.infrastructure.config;

import com.smart.smart_backend.application.port.out.JwtProviderPort;
import com.smart.smart_backend.application.port.out.PasswordEncoderPort;
import com.smart.smart_backend.application.port.out.RefreshTokenRepositoryPort;
import com.smart.smart_backend.application.port.out.UserRepositoryPort;
import com.smart.smart_backend.application.usecase.LoginUserService;
import com.smart.smart_backend.application.usecase.LogoutUserService;
import com.smart.smart_backend.application.usecase.RefreshSessionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Use Cases de autenticación.
 * Instancia los servicios de la capa de aplicación sin anotaciones de Spring,
 * manteniendo el dominio libre del framework.
 */
@Configuration
public class AuthUseCaseConfig {

    @Bean
    public LoginUserService loginUserService(UserRepositoryPort userRepositoryPort,
                                             PasswordEncoderPort passwordEncoderPort,
                                             JwtProviderPort jwtProviderPort,
                                             RefreshTokenRepositoryPort refreshTokenRepositoryPort) {
        return new LoginUserService(userRepositoryPort, passwordEncoderPort, jwtProviderPort, refreshTokenRepositoryPort);
    }

    @Bean
    public RefreshSessionService refreshSessionService(RefreshTokenRepositoryPort refreshTokenRepositoryPort,
                                                       UserRepositoryPort userRepositoryPort,
                                                       JwtProviderPort jwtProviderPort) {
        return new RefreshSessionService(refreshTokenRepositoryPort, userRepositoryPort, jwtProviderPort);
    }

    @Bean
    public LogoutUserService logoutUserService(RefreshTokenRepositoryPort refreshTokenRepositoryPort) {
        return new LogoutUserService(refreshTokenRepositoryPort);
    }
}
