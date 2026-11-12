package com.rafael.todo.module.auth.model.vo;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.rafael.todo.module.auth.exception.RefreshTokenException;
import com.rafael.todo.module.auth.exception.RefreshTokenExpirationException;
import com.rafael.todo.module.auth.exception.RefreshTokenIdException;
import com.rafael.todo.module.auth.exception.RefreshTokenUserIdException;

@DisplayName("Auth Value Objects Tests")
class AuthValueObjectsTest {

    @Nested
    @DisplayName("RefreshTokenId Tests")
    class RefreshTokenIdTests {

        @Test
        @DisplayName("Should create RefreshTokenId with valid positive Long")
        void shouldCreateWithValidLong() {
            var id = RefreshTokenId.of(10L);
            assertEquals(10L, id.value());
            assertEquals("10", id.toString());
        }

        @Test
        @DisplayName("Should create RefreshTokenId from valid numeric String")
        void shouldCreateFromValidString() {
            var id = RefreshTokenId.of("  42  ");
            assertEquals(42L, id.value());
        }

        @Test
        @DisplayName("Should throw RefreshTokenIdException when Long value is null")
        void shouldThrowWhenLongNull() {
            var ex = assertThrows(RefreshTokenIdException.class, () -> RefreshTokenId.of((Long) null));
            assertEquals("auth.refresh.token.id.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, -1L, -99L})
        @DisplayName("Should throw RefreshTokenIdException when Long value is zero or negative")
        void shouldThrowWhenNonPositive(long invalidId) {
            var ex = assertThrows(RefreshTokenIdException.class, () -> RefreshTokenId.of(invalidId));
            assertEquals("auth.refresh.token.id.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Should throw RefreshTokenIdException when String is null or blank")
        void shouldThrowWhenStringBlank(String input) {
            var ex = assertThrows(RefreshTokenIdException.class, () -> RefreshTokenId.of(input));
            assertEquals("auth.refresh.token.id.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw RefreshTokenIdException when String is not numeric")
        void shouldThrowWhenStringNotNumeric() {
            var ex = assertThrows(RefreshTokenIdException.class, () -> RefreshTokenId.of("not-a-number"));
            assertEquals("auth.refresh.token.id.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("RefreshTokenUserId Tests")
    class RefreshTokenUserIdTests {

        @Test
        @DisplayName("Should create RefreshTokenUserId from UUID and String")
        void shouldCreateValid() {
            var uuid = UUID.randomUUID();
            var fromUuid = RefreshTokenUserId.of(uuid);
            var fromString = RefreshTokenUserId.of("  " + uuid.toString() + "  ");

            assertEquals(uuid, fromUuid.value());
            assertEquals(uuid, fromString.value());
            assertEquals(uuid.toString(), fromUuid.toString());
        }

        @Test
        @DisplayName("Should throw RefreshTokenUserIdException when UUID is null")
        void shouldThrowWhenUuidNull() {
            var ex = assertThrows(RefreshTokenUserIdException.class, () -> RefreshTokenUserId.of((UUID) null));
            assertEquals("auth.user.id.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Should throw RefreshTokenUserIdException when String is null or blank")
        void shouldThrowWhenStringBlank(String input) {
            var ex = assertThrows(RefreshTokenUserIdException.class, () -> RefreshTokenUserId.of(input));
            assertEquals("auth.user.id.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw RefreshTokenUserIdException when String has invalid UUID format")
        void shouldThrowWhenInvalidUuidFormat() {
            var ex = assertThrows(RefreshTokenUserIdException.class, () -> RefreshTokenUserId.of("invalid-uuid"));
            assertEquals("auth.user.id.invalid.format", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("RefreshTokenHash Tests")
    class RefreshTokenHashTests {

        @Test
        @DisplayName("Should create and trim RefreshTokenHash")
        void shouldCreateAndTrim() {
            var hash = RefreshTokenHash.of("  token-hash-123  ");
            assertEquals("token-hash-123", hash.value());
            assertEquals("token-hash-123", hash.toString());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("Should throw RefreshTokenException when hash is null or blank")
        void shouldThrowWhenBlank(String input) {
            var ex = assertThrows(RefreshTokenException.class, () -> RefreshTokenHash.of(input));
            assertEquals("auth.refresh.token.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw RefreshTokenException when hash exceeds 255 characters")
        void shouldThrowWhenTooLong() {
            String longToken = "a".repeat(256);
            var ex = assertThrows(RefreshTokenException.class, () -> RefreshTokenHash.of(longToken));
            assertEquals("auth.refresh.token.too.long", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("RefreshTokenExpiration Tests")
    class RefreshTokenExpirationTests {

        @Test
        @DisplayName("Should create RefreshTokenExpiration from LocalDateTime and ISO String")
        void shouldCreateValid() {
            var future = LocalDateTime.now().plusDays(7);
            var fromDate = RefreshTokenExpiration.of(future);
            var fromString = RefreshTokenExpiration.of(future.toString());

            assertNotNull(fromDate.value());
            assertEquals(future, fromString.value());
            assertFalse(fromDate.isExpired());
        }

        @Test
        @DisplayName("Should detect expired RefreshTokenExpiration")
        void shouldDetectExpired() {
            var past = LocalDateTime.now().minusMinutes(5);
            var expiration = RefreshTokenExpiration.of(past);
            assertTrue(expiration.isExpired());
        }

        @Test
        @DisplayName("Should throw RefreshTokenExpirationException when LocalDateTime is null")
        void shouldThrowWhenNull() {
            var ex = assertThrows(RefreshTokenExpirationException.class,
                    () -> RefreshTokenExpiration.of((LocalDateTime) null));
            assertEquals("auth.expiration.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Should throw RefreshTokenExpirationException when ISO String is null or blank")
        void shouldThrowWhenStringBlank(String input) {
            var ex = assertThrows(RefreshTokenExpirationException.class, () -> RefreshTokenExpiration.of(input));
            assertEquals("auth.expiration.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw RefreshTokenExpirationException when ISO String is malformed")
        void shouldThrowWhenStringMalformed() {
            var ex = assertThrows(RefreshTokenExpirationException.class,
                    () -> RefreshTokenExpiration.of("not-a-date"));
            assertEquals("auth.expiration.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("RefreshTokenRevoked Tests")
    class RefreshTokenRevokedTests {

        @Test
        @DisplayName("Should create active and revoked instances and transition to revoked")
        void shouldCreateAndRevoke() {
            var active = RefreshTokenRevoked.active();
            assertFalse(active.isRevoked());
            assertEquals("false", active.toString());

            var revoked = active.revoke();
            assertTrue(revoked.isRevoked());
            assertEquals("true", revoked.toString());
        }

        @Test
        @DisplayName("Should throw RefreshTokenException when revoking an already revoked token")
        void shouldThrowWhenAlreadyRevoked() {
            var revoked = RefreshTokenRevoked.revoked();
            var ex = assertThrows(RefreshTokenException.class, revoked::revoke);
            assertEquals("auth.refresh.token.already.revoked", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }
}
