package com.smart.smart_backend.application.usecase.user;

import com.smart.smart_backend.application.dto.user.RegisterRequestDto;
import com.smart.smart_backend.application.dto.user.UserResponseDto;
import com.smart.smart_backend.application.mapper.user.UserMapper;
import com.smart.smart_backend.application.port.in.user.RegisterUserUseCase;
import com.smart.smart_backend.application.port.out.user.PasswordEncoderPort;
import com.smart.smart_backend.application.port.out.user.UserRepositoryPort;
import com.smart.smart_backend.domain.model.user.User;
import com.smart.smart_backend.domain.exception.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    public UserResponseDto register(RegisterRequestDto request) {
        if (userRepositoryPort.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        String encodedPassword = passwordEncoderPort.encode(request.password());

        User newUser = User.builder()
                .name(request.name())
                .email(request.email())
                .passwordHash(encodedPassword)
                .role("USER")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        User saveUser = userRepositoryPort.save(newUser);
        return UserMapper.toDto(saveUser);
    }
}
