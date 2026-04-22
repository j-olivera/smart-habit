package com.smart.smart_backend.infrastructure.adapter;

import com.smart.smart_backend.domain.model.user.User;
import com.smart.smart_backend.infrastructure.repository.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@ComponentScan(basePackages = "com.smart.smart_backend.infrastructure.mapper")
@Import(UserRepositoryAdapter.class)
class UserRepositoryAdapterTest {

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @BeforeEach
    void setUp() {
        jpaUserRepository.deleteAll();
    }

    @Test
    void saveAndExistsByEmail_ShouldWorkProperly() {
        User user = User.builder()
                .name("Alice")
                .email("alice@mail.com")
                .passwordHash("hash")
                .role("USER")
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        userRepositoryAdapter.save(user);

        assertThat(userRepositoryAdapter.existsByEmail("alice@mail.com")).isTrue();
        assertThat(userRepositoryAdapter.existsByEmail("bob@mail.com")).isFalse();
    }
}
