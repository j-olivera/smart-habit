package com.smart.smart_backend.infrastructure.controller;

import com.smart.smart_backend.application.port.in.report.GenerateWeeklyReportPort;
import com.smart.smart_backend.application.port.in.report.GetWeeklyReportByIdPort;
import com.smart.smart_backend.application.port.in.report.GetWeeklyReportsPort;
import com.smart.smart_backend.application.port.out.user.JwtProviderPort;
import com.smart.smart_backend.application.port.out.user.UserRepositoryPort;
import com.smart.smart_backend.domain.model.user.User;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeeklyReportController.class)
@Import({ SecurityConfig.class, JwtAuthenticationFilter.class })
@ActiveProfiles("test")
class WeeklyReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetWeeklyReportsPort getWeeklyReportsUseCase;

    @MockBean
    private GetWeeklyReportByIdPort getWeeklyReportByIdUseCase;

    @MockBean
    private GenerateWeeklyReportPort generateWeeklyReportUseCase;

    @MockBean
    private JwtProviderPort jwtProviderPort;

    @MockBean
    private UserRepositoryPort userRepositoryPort;

    @BeforeEach
    void setUp() {
        User mockUser = User.builder()
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
    void shouldListReports() throws Exception {
        when(getWeeklyReportsUseCase.execute(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenReportDoesNotExist() throws Exception {
        when(getWeeklyReportByIdUseCase.execute(anyLong(), anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reports/99"))
                .andExpect(status().isNotFound());
    }
}
