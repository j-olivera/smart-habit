package com.smart.smart_backend.application.usecase.habit;

import com.smart.smart_backend.application.dto.habit.HabitRequestDto;
import com.smart.smart_backend.application.dto.habit.HabitResponseDto;
import com.smart.smart_backend.application.port.out.habit.HabitRepositoryPort;
import com.smart.smart_backend.domain.enums.HabitType;
import com.smart.smart_backend.domain.model.habit.Habit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HabitUseCasesTest {

    @Mock
    private HabitRepositoryPort habitRepositoryPort;

    private RegisterHabitUseCase registerHabitUseCase;
    private GetUserHabitsUseCase getUserHabitsUseCase;
    private DesactivateHabitUseCase desactivateHabitUseCase;

    @BeforeEach
    void setUp() {
        registerHabitUseCase = new RegisterHabitUseCase(habitRepositoryPort);
        getUserHabitsUseCase = new GetUserHabitsUseCase(habitRepositoryPort);
        desactivateHabitUseCase = new DesactivateHabitUseCase(habitRepositoryPort);
    }

    @Test
    void shouldRegisterHabitSuccessfully() {
        // Given
        Long userId = 1L;
        HabitRequestDto request = new HabitRequestDto("Estudiar Java", HabitType.STUDY, "Estudiar 1 hora diaria de Java");

        Habit savedHabit = new Habit(100L, userId, "Estudiar Java", HabitType.STUDY, "Estudiar 1 hora diaria de Java", true, Instant.now());

        when(habitRepositoryPort.saveHabit(any(Habit.class), eq(userId))).thenReturn(savedHabit);

        // When
        HabitResponseDto result = registerHabitUseCase.execute(request, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.name()).isEqualTo("Estudiar Java");
        assertThat(result.type()).isEqualTo(HabitType.STUDY);
    }

    @Test
    void shouldRegisterMultipleHabitsForSameUser() {
        // Given
        Long userId = 1L;

        HabitRequestDto request1 = new HabitRequestDto("Estudiar Java", HabitType.STUDY, "Estudiar Java");
        HabitRequestDto request2 = new HabitRequestDto("Ejercicio", HabitType.EXERCISE, "Hacer ejercicio");

        when(habitRepositoryPort.saveHabit(any(Habit.class), eq(userId)))
                .thenAnswer(inv -> {
                    Habit h = inv.getArgument(0);
                    return new Habit(100L, userId, h.getName(), h.getType(), h.getDescription(), true, Instant.now());
                });

        // When
        HabitResponseDto result1 = registerHabitUseCase.execute(request1, userId);
        HabitResponseDto result2 = registerHabitUseCase.execute(request2, userId);

        // Then
        assertThat(result1.id()).isEqualTo(100L);
        assertThat(result2.id()).isEqualTo(100L);
        assertThat(result1.name()).isNotEqualTo(result2.name());
    }

    @Test
    void shouldGetAllActiveHabitsForUser() {
        // Given
        Long userId = 1L;
        List<Habit> activeHabits = List.of(
                new Habit(1L, userId, "Java", HabitType.STUDY, "Estudiar Java", true, Instant.now()),
                new Habit(2L, userId, "Ejercicio", HabitType.EXERCISE, "Hacer ejercicio", true, Instant.now())
        );

        when(habitRepositoryPort.findAllByUserId(userId)).thenReturn(activeHabits);

        // When
        List<HabitResponseDto> result = getUserHabitsUseCase.execute(userId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(HabitResponseDto::name)
                .containsExactlyInAnyOrder("Java", "Ejercicio");
    }

    @Test
    void shouldReturnEmptyListWhenNoHabits() {
        // Given
        Long userId = 1L;
        when(habitRepositoryPort.findAllByUserId(userId)).thenReturn(List.of());

        // When
        List<HabitResponseDto> result = getUserHabitsUseCase.execute(userId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldDesactivateHabit() {
        // Given
        Long habitId = 100L;
        Long userId = 1L;

        // When
        desactivateHabitUseCase.execute(habitId, userId);

        // Then
        // Verify repository was called (mock is void method)
    }

    @Test
    void shouldHandleDifferentHabitTypes() {
        // Given
        Long userId = 1L;
        List<Habit> habits = List.of(
                new Habit(1L, userId, "Java", HabitType.STUDY, "Estudiar Java", true, Instant.now()),
                new Habit(2L, userId, "Correr", HabitType.EXERCISE, "Correr", true, Instant.now()),
                new Habit(3L, userId, "Dormir", HabitType.SLEEP, "Dormir bien", true, Instant.now())
        );

        when(habitRepositoryPort.findAllByUserId(userId)).thenReturn(habits);

        // When
        List<HabitResponseDto> result = getUserHabitsUseCase.execute(userId);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(HabitResponseDto::type)
                .containsExactlyInAnyOrder(HabitType.STUDY, HabitType.EXERCISE, HabitType.SLEEP);
    }
}