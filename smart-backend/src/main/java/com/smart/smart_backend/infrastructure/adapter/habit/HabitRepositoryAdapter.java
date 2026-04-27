package com.smart.smart_backend.infrastructure.adapter.habit;

import com.smart.smart_backend.application.port.out.habit.HabitRepositoryPort;
import com.smart.smart_backend.domain.model.habit.Habit;
import com.smart.smart_backend.infrastructure.mapper.habit.HabitEntityMapper;
import com.smart.smart_backend.infrastructure.repository.habit.JpaHabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HabitRepositoryAdapter implements HabitRepositoryPort {

    private final JpaHabitRepository jpaHabitRepository;
    private final HabitEntityMapper habitEntityMapper;

    @Override
    public Habit findById(Long id) {
        return jpaHabitRepository.findById(id)
                .map(habitEntityMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Habit saveHabit(Habit habit, Long userId) {
        var entity = habitEntityMapper.toEntity(habit, userId);
        var saved = jpaHabitRepository.save(entity);
        return habitEntityMapper.toDomain(saved);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaHabitRepository.existsById(id);
    }

    @Override
    public void desactivateHabit(Long id, Long userId) {
        jpaHabitRepository.findByIdAndUserId(id, userId).ifPresent(entity -> {
            entity.setActive(false);
            jpaHabitRepository.save(entity);
        });
    }

    @Override
    public List<Habit> findAllByUserId(Long userId) {
        return jpaHabitRepository.findAllByUserIdAndActiveTrue(userId).stream()
                .map(habitEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Habit> findByIdAndUserId(Long id, Long userId) {
        return jpaHabitRepository.findByIdAndUserId(id, userId)
                .map(habitEntityMapper::toDomain);
    }
}
