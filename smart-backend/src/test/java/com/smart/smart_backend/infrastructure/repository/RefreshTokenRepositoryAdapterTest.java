package com.smart.smart_backend.infrastructure.repository;

import com.smart.smart_backend.domain.RefreshToken;
import com.smart.smart_backend.domain.User;
import com.smart.smart_backend.infrastructure.adapter.RefreshTokenRepositoryAdapter;
import com.smart.smart_backend.infrastructure.mapper.RefreshTokenEntityMapperImpl;
import com.smart.smart_backend.infrastructure.model.UserEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({ RefreshTokenRepositoryAdapter.class, RefreshTokenEntityMapperImpl.class })
class RefreshTokenRepositoryAdapterTest {

    @Autowired
    private RefreshTokenRepositoryAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    private UserEntity savedUser;

    @BeforeEach
    void setUp() {
        UserEntity user = UserEntity.builder()
                .name("Test User")
                .email("test@test.com")
                .passwordHash("hash")
                .role("USER")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        savedUser = entityManager.persistAndFlush(user);
    }

    @Test
    void shouldSaveAndFindRefreshToken() {
        // Arrange
        RefreshToken token = RefreshToken.builder()
                .userId(savedUser.getId())
                .tokenHash("some-random-uuid")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        // Act
        RefreshToken savedToken = adapter.save(token);
        Optional<RefreshToken> foundToken = adapter.findByTokenHash("some-random-uuid");

        // Assert
        assertThat(savedToken.getId()).isNotNull();
        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getUserId()).isEqualTo(savedUser.getId());
        assertThat(foundToken.get().getTokenHash()).isEqualTo("some-random-uuid");
        assertThat(foundToken.get().getRevoked()).isFalse();
    }
}
