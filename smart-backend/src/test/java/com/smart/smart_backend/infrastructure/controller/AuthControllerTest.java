package com.smart.smart_backend.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.smart_backend.application.dto.user.RegisterRequestDto;
import com.smart.smart_backend.application.dto.user.UserResponseDto;
import com.smart.smart_backend.application.dto.auth.AuthResultDto;
import com.smart.smart_backend.application.dto.auth.LoginRequestDto;
import com.smart.smart_backend.application.port.in.user.LoginUserUseCase;
import com.smart.smart_backend.application.port.in.user.LogoutUserUseCase;
import com.smart.smart_backend.application.port.in.user.RefreshSessionUseCase;
import com.smart.smart_backend.application.port.in.user.RegisterUserUseCase;
import com.smart.smart_backend.application.port.out.JwtProviderPort;
import com.smart.smart_backend.application.port.out.UserRepositoryPort;
import com.smart.smart_backend.domain.exception.EmailAlreadyExistsException;
import com.smart.smart_backend.infrastructure.security.JwtAdapter;
import com.smart.smart_backend.infrastructure.security.JwtAuthenticationFilter;
import com.smart.smart_backend.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({ SecurityConfig.class, JwtAuthenticationFilter.class, JwtAdapter.class })
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private RegisterUserUseCase registerUserUseCase;
        @MockBean
        private LoginUserUseCase loginUserUseCase;
        @MockBean
        private RefreshSessionUseCase refreshSessionUseCase;
        @MockBean
        private LogoutUserUseCase logoutUserUseCase;
        @MockBean
        private UserRepositoryPort userRepositoryPort;
        @MockBean
        private JwtProviderPort jwtProviderPort;

        @Autowired
        private ObjectMapper objectMapper;

        // ──────────────────────────────────────────────
        // /register
        // ──────────────────────────────────────────────

        @Test
        void registerUser_WhenValidRequest_ShouldReturnCreated() throws Exception {
                RegisterRequestDto request = new RegisterRequestDto("John", "john@mail.com", "secure123");
                UserResponseDto response = new UserResponseDto(
                                1L, "John", "john@mail.com", "USER", LocalDateTime.now(), true);
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
                                .andExpect(jsonPath("$.message")
                                                .value("El email john@mail.com ya se encuentra registrado."));
        }

        // ──────────────────────────────────────────────
        // /login
        // ──────────────────────────────────────────────

        @Test
        void login_WhenValidCredentials_ShouldReturnTokenAndSetCookie() throws Exception {
                LoginRequestDto request = new LoginRequestDto("john@mail.com", "secure123");
                when(loginUserUseCase.login(any(LoginRequestDto.class)))
                                .thenReturn(new AuthResultDto("jwt.access.token", "refresh-uuid"));

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").value("jwt.access.token"))
                                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                                                containsString("refreshToken=refresh-uuid")))
                                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                                                containsString("Path=/api/auth/refresh")));
        }

        @Test
        void login_WhenInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
                LoginRequestDto request = new LoginRequestDto("john@mail.com", "wrong");
                when(loginUserUseCase.login(any(LoginRequestDto.class)))
                                .thenThrow(new com.smart.smart_backend.domain.exception.InvalidCredentialsException());

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.message").value("Invalid email or password"));
        }

        // ──────────────────────────────────────────────
        // /refresh
        // ──────────────────────────────────────────────

        @Test
        void refresh_WhenValidCookie_ShouldReturnNewToken() throws Exception {
                when(refreshSessionUseCase.refresh(anyString()))
                                .thenReturn(new AuthResultDto("new.jwt.token", "new-refresh-uuid"));

                mockMvc.perform(post("/api/auth/refresh")
                                .cookie(new jakarta.servlet.http.Cookie("refreshToken", "old-refresh-uuid")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").value("new.jwt.token"))
                                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                                                containsString("refreshToken=new-refresh-uuid")));
        }

        @Test
        void refresh_WhenNoCookie_ShouldReturnUnauthorized() throws Exception {
                mockMvc.perform(post("/api/auth/refresh"))
                                .andExpect(status().isUnauthorized());
        }

        // ──────────────────────────────────────────────
        // /logout
        // ──────────────────────────────────────────────

        @Test
        void logout_WhenValidCookie_ShouldClearCookieAndReturnOk() throws Exception {
                doNothing().when(logoutUserUseCase).logout(anyString());

                mockMvc.perform(post("/api/auth/logout")
                                .cookie(new jakarta.servlet.http.Cookie("refreshToken", "some-uuid")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Logged out successfully"))
                                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("refreshToken=")))
                                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")));
        }
}
