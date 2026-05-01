package com.smart.smart_backend.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.smart_backend.application.dto.habit.HabitRequestDto;
import com.smart.smart_backend.application.dto.habit.HabitResponseDto;
import com.smart.smart_backend.application.port.in.habit.CreateHabit;
import com.smart.smart_backend.application.port.in.habit.DesactivateHabit;
import com.smart.smart_backend.application.port.in.habit.GetUserHabits;
import com.smart.smart_backend.application.port.out.user.JwtProviderPort;
import com.smart.smart_backend.application.port.out.user.UserRepositoryPort;
import com.smart.smart_backend.domain.enums.HabitType;
import com.smart.smart_backend.domain.model.user.User;
import com.smart.smart_backend.infrastructure.security.JwtAuthenticationFilter;
import com.smart.smart_backend.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HabitController.class)
@Import({ SecurityConfig.class, JwtAuthenticationFilter.class })
class HabitControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateHabit createHabitUseCase;
    @MockBean
    private GetUserHabits getUserHabitsUseCase;
    @MockBean
    private DesactivateHabit desactivateHabitUseCase;

    // Security Mocks needed by JwtAuthenticationFilter
    @MockBean
    private JwtProviderPort jwtProviderPort;
    @MockBean
    private UserRepositoryPort userRepositoryPort;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@test.com")
                .role("USER")
                .active(true)
                .build();

        // Manual authentication for the SecurityContext
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        var auth = new UsernamePasswordAuthenticationToken(mockUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void shouldCreateHabit() throws Exception {
        HabitRequestDto request = new HabitRequestDto("Run", HabitType.EXERCISE, "Daily run");
        HabitResponseDto response = new HabitResponseDto(10L, 1L, "Run", HabitType.EXERCISE, "Daily run", true,
                Instant.now());

        when(createHabitUseCase.execute(any(HabitRequestDto.class), eq(1L))).thenReturn(response);

        mockMvc.perform(post("/api/habits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Run"));
    }

    @Test
    void shouldGetHabits() throws Exception {
        HabitResponseDto h1 = new HabitResponseDto(10L, 1L, "Run", HabitType.EXERCISE, "desc", true, Instant.now());
        when(getUserHabitsUseCase.execute(1L)).thenReturn(List.of(h1));

        mockMvc.perform(get("/api/habits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Run"));
    }
}
