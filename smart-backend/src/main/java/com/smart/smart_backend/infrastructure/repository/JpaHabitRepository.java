package com.smart.smart_backend.infrastructure.repository;

import com.smart.smart_backend.infrastructure.model.HabitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaHabitRepository extends JpaRepository<HabitEntity, Long> {
    List<HabitEntity> findAllByUserIdAndActiveTrue(Long userId);
    Optional<HabitEntity> findByIdAndUserId(Long id, Long userId);
}
