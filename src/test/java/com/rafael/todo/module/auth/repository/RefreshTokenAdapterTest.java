package com.rafael.todo.module.auth.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafael.todo.module.auth.model.RefreshTokenModel;
import com.rafael.todo.module.auth.model.vo.RefreshTokenExpiration;
import com.rafael.todo.module.auth.model.vo.RefreshTokenHash;
import com.rafael.todo.module.auth.model.vo.RefreshTokenId;
import com.rafael.todo.module.auth.model.vo.RefreshTokenRevoked;
import com.rafael.todo.module.auth.model.vo.RefreshTokenUserId;
import com.rafael.todo.module.auth.repository.adapter.imp.RefreshTokenAdapter;
import com.rafael.todo.module.auth.repository.entity.RefreshTokenEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenAdapter - Persistence Adapter Tests")
class RefreshTokenAdapterTest {

    @Mock
    private IRefreshTokenRepository repository;

    @InjectMocks
    private RefreshTokenAdapter adapter;

    private UUID userId;
    private RefreshTokenEntity sampleEntity;
    private RefreshTokenModel sampleModel;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        var now = LocalDateTime.now();
        var expiresAt = now.plusDays(7);

        sampleEntity = RefreshTokenEntity.builder()
                .id(1L)
                .userId(userId)
                .tokenHash("hash-123")
                .expiresAt(expiresAt)
                .isRevoked(false)
                .createdAt(now)
                .build();

        sampleModel = RefreshTokenModel.reconstitute(
                RefreshTokenId.of(1L),
                RefreshTokenUserId.of(userId),
                RefreshTokenHash.of("hash-123"),
                RefreshTokenExpiration.of(expiresAt),
                RefreshTokenRevoked.active(),
                now
        );
    }

    @Test
    @DisplayName("create & update - Should save RefreshTokenEntity and return mapped RefreshTokenModel")
    void shouldCreateAndUpdate() {
        when(repository.save(any(RefreshTokenEntity.class))).thenReturn(sampleEntity);

        var created = adapter.create(sampleModel);
        assertEquals(1L, created.getIdValue());
        assertEquals("hash-123", created.getTokenHashValue());

        var updated = adapter.update(sampleModel);
        assertEquals(1L, updated.getIdValue());
    }

    @Test
    @DisplayName("findById & findByTokenHash - Should return Optional<RefreshTokenModel>")
    void shouldFindByIdAndTokenHash() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(repository.findByTokenHash("hash-123")).thenReturn(Optional.of(sampleEntity));

        var byId = adapter.findById(1L);
        var byHash = adapter.findByTokenHash("hash-123");

        assertTrue(byId.isPresent());
        assertEquals(1L, byId.get().getIdValue());
        assertTrue(byHash.isPresent());
        assertEquals("hash-123", byHash.get().getTokenHashValue());
    }

    @Test
    @DisplayName("findAllByUserId & findAllFiltered - Should map entity list to domain models")
    void shouldFindAllByUserIdAndFiltered() {
        when(repository.findAllByUserId(userId)).thenReturn(List.of(sampleEntity));
        when(repository.findAllFiltered(userId, false)).thenReturn(List.of(sampleEntity));

        assertEquals(1, adapter.findAllByUserId(userId).size());
        assertEquals(1, adapter.findAllFiltered(userId, false).size());
    }

    @Test
    @DisplayName("delete & deleteByUserId - Should delegate deletion to repository")
    void shouldDeleteAndDeleteByUserId() {
        adapter.delete(1L);
        verify(repository).deleteById(1L);

        adapter.deleteByUserId(userId);
        verify(repository).deleteByUserId(userId);
    }
}

