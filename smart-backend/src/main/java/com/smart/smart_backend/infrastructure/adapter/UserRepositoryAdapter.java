package com.smart.smart_backend.infrastructure.adapter;

import com.smart.smart_backend.application.port.out.user.UserRepositoryPort;
import com.smart.smart_backend.domain.model.user.User;
import com.smart.smart_backend.infrastructure.mapper.UserEntityMapper;
import com.smart.smart_backend.infrastructure.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpaUserRepository;
    private final UserEntityMapper userEntityMapper;

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(userEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id)
                .map(userEntityMapper::toDomain);
    }

    @Override
    public User save(User user) {
        var entity = userEntityMapper.toEntity(user);
        var savedEntity = jpaUserRepository.save(entity);
        return userEntityMapper.toDomain(savedEntity);
    }
}
