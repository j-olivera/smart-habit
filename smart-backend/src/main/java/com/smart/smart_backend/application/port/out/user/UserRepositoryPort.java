package com.smart.smart_backend.application.port.out.user;

import com.smart.smart_backend.domain.model.user.User;
import java.util.Optional;

public interface UserRepositoryPort {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    User save(User user);
}
