package com.smart.smart_backend.application.usecase;

import com.smart.smart_backend.application.dto.auth.AuthResultDto;
import com.smart.smart_backend.application.dto.auth.LoginRequestDto;
import com.smart.smart_backend.application.port.out.JwtProviderPort;
import com.smart.smart_backend.application.port.out.PasswordEncoderPort;
import com.smart.smart_backend.application.port.out.RefreshTokenRepositoryPort;
import com.smart.smart_backend.application.port.out.UserRepositoryPort;
import com.smart.smart_backend.domain.model.token.RefreshToken;
import com.smart.smart_backend.domain.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCasesTest {

    @Mock private UserRepositoryPort userRepositoryPort;
    @Mock private PasswordEncoderPort passwordEncoderPort;
    @Mock private JwtProviderPort jwtProviderPort;
    @Mock private RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    private LoginUserService loginUserService;
    private RefreshSessionService refreshSessionService;
    private LogoutUserService logoutUserService;

    private User validUser;

    @BeforeEach
    void setUp() {
        loginUserService = new LoginUserService(userRepositoryPort, passwordEncoderPort, jwtProviderPort, refreshTokenRepositoryPort);
        refreshSessionService = new RefreshSessionService(refreshTokenRepositoryPort, userRepositoryPort, jwtProviderPort);
        logoutUserService = new LogoutUserService(refreshTokenRepositoryPort);

        validUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .passwordHash("hashedPwd")
                .active(true)
                .build();
    }

    @Test
    void shouldLoginSuccessfully() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto("test@test.com", "12345");
        when(userRepositoryPort.findByEmail("test@test.com")).thenReturn(Optional.of(validUser));
        when(passwordEncoderPort.matches("12345", "hashedPwd")).thenReturn(true);
        when(jwtProviderPort.generateToken(validUser)).thenReturn("jwt.token.string");
        
        RefreshToken savedToken = RefreshToken.builder().tokenHash("new-uuid").build();
        when(refreshTokenRepositoryPort.save(any(RefreshToken.class))).thenReturn(savedToken);

        // Act
        AuthResultDto result = loginUserService.login(request);

        // Assert
        assertThat(result.accessToken()).isEqualTo("jwt.token.string");
        assertThat(result.refreshToken()).isEqualTo("new-uuid");

        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepositoryPort).save(tokenCaptor.capture());
        assertThat(tokenCaptor.getValue().getUserId()).isEqualTo(1L);
    }

    @Test
    void shouldFailLoginWhenPasswordWrong() {
        // Arrange
        LoginRequestDto request = new LoginRequestDto("test@test.com", "wrong");
        when(userRepositoryPort.findByEmail("test@test.com")).thenReturn(Optional.of(validUser));
        when(passwordEncoderPort.matches("wrong", "hashedPwd")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> loginUserService.login(request))
                .isInstanceOf(com.smart.smart_backend.domain.exception.InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void shouldRefreshSuccessfully() {
        // Arrange
        RefreshToken oldToken = RefreshToken.builder()
                .userId(1L)
                .tokenHash("old-uuid")
                .expiresAt(LocalDateTime.now().plusDays(1))
                .revoked(false)
                .build();

        when(refreshTokenRepositoryPort.findByTokenHash("old-uuid")).thenReturn(Optional.of(oldToken));
        when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(validUser));
        when(jwtProviderPort.generateToken(validUser)).thenReturn("new-jwt-token");
        
        RefreshToken newSavedToken = RefreshToken.builder().tokenHash("new-uuid").build();
        lenient().when(refreshTokenRepositoryPort.save(oldToken)).thenReturn(oldToken);
        when(refreshTokenRepositoryPort.save(any(RefreshToken.class))).thenReturn(newSavedToken);

        // Act
        AuthResultDto result = refreshSessionService.refresh("old-uuid");

        // Assert
        assertThat(result.accessToken()).isEqualTo("new-jwt-token");
        assertThat(oldToken.getRevoked()).isTrue(); // Verifies token rotation
    }

    @Test
    void shouldLogoutSuccessfully() {
        // Arrange
        RefreshToken activeToken = RefreshToken.builder()
                .tokenHash("some-uuid")
                .revoked(false)
                .build();
        when(refreshTokenRepositoryPort.findByTokenHash("some-uuid")).thenReturn(Optional.of(activeToken));

        // Act
        logoutUserService.logout("some-uuid");

        // Assert
        assertThat(activeToken.getRevoked()).isTrue();
        verify(refreshTokenRepositoryPort).save(activeToken);
    }
}
