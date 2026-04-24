package com.smart.smart_backend.application.usecase;

import com.smart.smart_backend.application.dto.user.RegisterRequestDto;
import com.smart.smart_backend.application.dto.user.UserResponseDto;
import com.smart.smart_backend.application.port.out.user.PasswordEncoderPort;
import com.smart.smart_backend.application.port.out.user.UserRepositoryPort;
import com.smart.smart_backend.application.usecase.user.RegisterUserService;
import com.smart.smart_backend.domain.model.user.User;
import com.smart.smart_backend.domain.exception.EmailAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private RegisterUserService registerUserService;

    private RegisterRequestDto validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequestDto("Alan", "alan@mail.com", "secret123");
    }

    @Test
    void register_WhenEmailDoesNotExist_ShouldSaveAndReturnUser() {
        // Arrange
        when(userRepositoryPort.existsByEmail("alan@mail.com")).thenReturn(false);
        when(passwordEncoderPort.encode("secret123")).thenReturn("hashedsecret");
        
        User savedUserFromDb = User.builder()
                .id(1L)
                .name("Alan")
                .email("alan@mail.com")
                .passwordHash("hashedsecret")
                .role("USER")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
                
        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUserFromDb);

        // Act
        UserResponseDto result = registerUserService.register(validRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Alan");
        
        verify(userRepositoryPort).existsByEmail("alan@mail.com");
        verify(passwordEncoderPort).encode("secret123");
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    void register_WhenEmailAlreadyExists_ShouldThrowException() {
        // Arrange
        when(userRepositoryPort.existsByEmail("alan@mail.com")).thenReturn(true);

        // Act & Assert
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, 
            () -> registerUserService.register(validRequest));
        
        assertThat(exception.getMessage()).contains("alan@mail.com");
        verify(passwordEncoderPort, never()).encode(anyString());
        verify(userRepositoryPort, never()).save(any(User.class));
    }
}
