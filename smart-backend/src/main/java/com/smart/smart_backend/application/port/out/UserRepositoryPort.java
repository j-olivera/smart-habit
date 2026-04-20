package com.smart.smart_backend.application.port.out;

import com.smart.smart_backend.domain.User;
import java.util.Optional;

public interface UserRepositoryPort {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    User save(User user);
}
