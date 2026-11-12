package com.rafael.todo.module.auth.model;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rafael.todo.module.auth.exception.RefreshTokenException;
import com.rafael.todo.module.auth.model.vo.RefreshTokenExpiration;
import com.rafael.todo.module.auth.model.vo.RefreshTokenHash;
import com.rafael.todo.module.auth.model.vo.RefreshTokenId;
import com.rafael.todo.module.auth.model.vo.RefreshTokenRevoked;
import com.rafael.todo.module.auth.model.vo.RefreshTokenUserId;

@DisplayName("RefreshTokenModel Domain Tests")
class RefreshTokenModelTest {

    @Test
    @DisplayName("Should create new active RefreshTokenModel with null id and current createdAt")
    void shouldCreateNewRefreshTokenModel() {
        var userId = UUID.randomUUID();
        var expiresAt = LocalDateTime.now().plusDays(7);

        var model = RefreshTokenModel.create(
                RefreshTokenUserId.of(userId),
                RefreshTokenHash.of("refresh-token-hash"),
                RefreshTokenExpiration.of(expiresAt)
        );

        assertNull(model.getIdValue());
        assertEquals(userId, model.getUserIdValue());
        assertEquals("refresh-token-hash", model.getTokenHashValue());
        assertEquals(expiresAt, model.getExpiresAtValue());
        assertFalse(model.isRevokedValue());
        assertFalse(model.isExpired());
        assertNotNull(model.getCreatedAt());
        assertDoesNotThrow(model::validateActive);
    }

    @Test
    @DisplayName("Should reconstitute RefreshTokenModel preserving all persisted values")
    void shouldReconstituteRefreshTokenModel() {
        var userId = UUID.randomUUID();
        var createdAt = LocalDateTime.now().minusDays(1);
        var expiresAt = LocalDateTime.now().plusDays(6);

        var model = RefreshTokenModel.reconstitute(
                RefreshTokenId.of(5L),
                RefreshTokenUserId.of(userId),
                RefreshTokenHash.of("persisted-hash"),
                RefreshTokenExpiration.of(expiresAt),
                RefreshTokenRevoked.of(false),
                createdAt
        );

        assertEquals(5L, model.getIdValue());
        assertEquals(userId, model.getUserIdValue());
        assertEquals("persisted-hash", model.getTokenHashValue());
        assertEquals(expiresAt, model.getExpiresAtValue());
        assertFalse(model.isRevokedValue());
        assertEquals(createdAt, model.getCreatedAt());
    }

    @Test
    @DisplayName("Should revoke RefreshTokenModel immutably (copy-on-write)")
    void shouldRevokeImmutably() {
        var original = RefreshTokenModel.reconstitute(
                RefreshTokenId.of(1L),
                RefreshTokenUserId.of(UUID.randomUUID()),
                RefreshTokenHash.of("token-hash"),
                RefreshTokenExpiration.of(LocalDateTime.now().plusDays(7)),
                RefreshTokenRevoked.active(),
                LocalDateTime.now()
        );

        var revoked = original.revoke();

        assertNotSame(original, revoked);
        assertFalse(original.isRevokedValue());
        assertTrue(revoked.isRevokedValue());
        assertEquals(original.getIdValue(), revoked.getIdValue());
    }

    @Test
    @DisplayName("validateActive should throw 401 when token is revoked")
    void validateActiveShouldThrowWhenRevoked() {
        var revokedModel = RefreshTokenModel.reconstitute(
                RefreshTokenId.of(1L),
                RefreshTokenUserId.of(UUID.randomUUID()),
                RefreshTokenHash.of("token-hash"),
                RefreshTokenExpiration.of(LocalDateTime.now().plusDays(7)),
                RefreshTokenRevoked.revoked(),
                LocalDateTime.now()
        );

        var ex = assertThrows(RefreshTokenException.class, revokedModel::validateActive);
        assertEquals("auth.refresh.token.revoked", ex.getCode());
        assertEquals(401, ex.getStatusCode());
    }

    @Test
    @DisplayName("validateActive should throw 401 when token is expired")
    void validateActiveShouldThrowWhenExpired() {
        var expiredModel = RefreshTokenModel.reconstitute(
                RefreshTokenId.of(1L),
                RefreshTokenUserId.of(UUID.randomUUID()),
                RefreshTokenHash.of("token-hash"),
                RefreshTokenExpiration.of(LocalDateTime.now().minusMinutes(10)),
                RefreshTokenRevoked.active(),
                LocalDateTime.now().minusDays(7)
        );

        var ex = assertThrows(RefreshTokenException.class, expiredModel::validateActive);
        assertEquals("auth.refresh.token.expired", ex.getCode());
        assertEquals(401, ex.getStatusCode());
    }
}

