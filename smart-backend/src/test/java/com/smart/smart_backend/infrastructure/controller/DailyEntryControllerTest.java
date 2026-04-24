package com.smart.smart_backend.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;
import com.smart.smart_backend.application.dto.habit.WeeklyEntriesReportDto;
import com.smart.smart_backend.application.port.in.habit.GetDailyEntryUseCase;
import com.smart.smart_backend.application.port.in.habit.GetWeeklyEntriesUseCase;
import com.smart.smart_backend.application.port.in.registers.CreateDailyEntry;
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
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DailyEntryController.class)
@Import({ SecurityConfig.class, JwtAuthenticationFilter.class })
class DailyEntryControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private CreateDailyEntry createDailyEntryUseCase;
    @MockBean private GetDailyEntryUseCase getDailyEntryUseCase;
    @MockBean private GetWeeklyEntriesUseCase getWeeklyEntriesUseCase;

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
    void shouldGetEntryByDate() throws Exception {
        LocalDate date = LocalDate.of(2026, 4, 24);
        DailyEntryWithLogsResult response = DailyEntryWithLogsResult.builder()
                .id(100L)
                .userId(1L)
                .date(date)
                .studyLogs(List.of())
                .exerciseLogs(List.of())
                .nutritionLogs(List.of())
                .moodLogs(List.of())
                .sleepLogs(List.of())
                .build();

        when(getDailyEntryUseCase.execute(1L, date)).thenReturn(response);

        mockMvc.perform(get("/api/daily-entries/2026-04-24"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.date").value("2026-04-24"));
    }

    @Test
    void shouldGetWeeklyReport() throws Exception {
        LocalDate start = LocalDate.of(2026, 4, 20);
        WeeklyEntriesReportDto response = WeeklyEntriesReportDto.builder()
                .userId(1L)
                .weekStart(start)
                .weekEnd(start.plusDays(6))
                .dailyEntries(List.of())
                .build();

        when(getWeeklyEntriesUseCase.execute(eq(1L), any(LocalDate.class))).thenReturn(response);

        mockMvc.perform(get("/api/daily-entries/weekly")
                .param("weekStart", "2026-04-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weekStart").value("2026-04-20"));
    }
}
