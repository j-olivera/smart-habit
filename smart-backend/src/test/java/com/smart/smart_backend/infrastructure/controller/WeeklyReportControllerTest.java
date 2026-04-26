package com.smart.smart_backend.infrastructure.controller;

import com.smart.smart_backend.application.dto.report.WeeklyReportResult;
import com.smart.smart_backend.application.port.in.report.GenerateWeeklyReportPort;
import com.smart.smart_backend.application.port.out.user.JwtProviderPort;
import com.smart.smart_backend.application.port.out.user.UserRepositoryPort;
import com.smart.smart_backend.domain.model.user.User;
import com.smart.smart_backend.domain.exception.InsufficientDataException;
import com.smart.smart_backend.infrastructure.security.JwtAuthenticationFilter;
import com.smart.smart_backend.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeeklyReportController.class)
@Import({ SecurityConfig.class, JwtAuthenticationFilter.class })
class WeeklyReportControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private GenerateWeeklyReportPort generateWeeklyReportUseCase;

    // Security Mocks
    @MockBean private JwtProviderPort jwtProviderPort;
    @MockBean private UserRepositoryPort userRepositoryPort;

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

        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        var auth = new UsernamePasswordAuthenticationToken(mockUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void shouldGenerateWeeklyReport() throws Exception {
        LocalDate weekStart = LocalDate.of(2026, 4, 20);
        WeeklyReportResult result = new WeeklyReportResult(
                1L,
                weekStart,
                weekStart.plusDays(6),
                "## RESUMEN GENERAL\nEsta semana fue muy productiva...",
                Instant.now()
        );

        when(generateWeeklyReportUseCase.execute(any())).thenReturn(result);

        mockMvc.perform(post("/api/reports/weekly/generate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.weekStart").value("2026-04-20"))
                .andExpect(jsonPath("$.weekEnd").value("2026-04-26"))
                .andExpect(jsonPath("$.aiContent").exists());
    }

    @Test
    void shouldReturn422WhenInsufficientData() throws Exception {
        when(generateWeeklyReportUseCase.execute(any()))
                .thenThrow(new InsufficientDataException(2, 3));

        mockMvc.perform(post("/api/reports/weekly/generate"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").exists());
    }
}