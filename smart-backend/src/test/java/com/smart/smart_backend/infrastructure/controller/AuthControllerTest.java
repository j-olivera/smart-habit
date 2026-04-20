package com.smart.smart_backend.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.smart_backend.application.dto.RegisterRequestDto;
import com.smart.smart_backend.application.dto.UserResponseDto;
import com.smart.smart_backend.application.port.in.RegisterUserUseCase;
import com.smart.smart_backend.domain.exception.EmailAlreadyExistsException;
import com.smart.smart_backend.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegisterUserUseCase registerUserUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_WhenValidRequest_ShouldReturnCreated() throws Exception {
        RegisterRequestDto request = new RegisterRequestDto("John", "john@mail.com", "secure123");
        UserResponseDto response = new UserResponseDto(
                1L, "John", "john@mail.com", "USER", LocalDateTime.now(), true
        );

        when(registerUserUseCase.register(any(RegisterRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("john@mail.com"));
    }

    @Test
    void registerUser_WhenInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Missing name and short password
        RegisterRequestDto request = new RegisterRequestDto("", "john@mail.com", "123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message.name").exists())
                .andExpect(jsonPath("$.message.password").exists());
    }

    @Test
    void registerUser_WhenEmailExists_ShouldReturnConflict() throws Exception {
        RegisterRequestDto request = new RegisterRequestDto("John", "john@mail.com", "secure123");

        when(registerUserUseCase.register(any(RegisterRequestDto.class)))
                .thenThrow(new EmailAlreadyExistsException("john@mail.com"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El email john@mail.com ya se encuentra registrado."));
    }
}
