package com.rafael.todo.module.user.model.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.rafael.todo.module.user.exception.UserAccountException;
import com.rafael.todo.module.user.exception.UserEmailException;
import com.rafael.todo.module.user.exception.UserNameException;
import com.rafael.todo.module.user.exception.UserPasswordException;
import com.rafael.todo.module.user.exception.UserRoleException;
import com.rafael.todo.module.user.exception.UserUuidException;
import com.rafael.todo.module.user.model.enums.UserRoleEnum;

@DisplayName("User Value Objects - Unit Tests")
class UserValueObjectsTest {

    @Nested
    @DisplayName("UserId Value Object")
    class UserIdTests {

        @Test
        @DisplayName("Should create random UserId")
        void shouldCreateRandomUserId() {
            UserId id = UserId.random();
            assertNotNull(id);
            assertNotNull(id.value());
            assertEquals(id.value().toString(), id.toString());
        }

        @Test
        @DisplayName("Should create UserId from valid UUID and String")
        void shouldCreateFromValidUuidAndString() {
            UUID rawUuid = UUID.randomUUID();
            UserId fromUuid = UserId.of(rawUuid);
            UserId fromString = UserId.of("  " + rawUuid.toString() + "  ");

            assertEquals(rawUuid, fromUuid.value());
            assertEquals(rawUuid, fromString.value());
        }

        @Test
        @DisplayName("Should throw UserUuidException when UUID is null")
        void shouldThrowWhenUuidIsNull() {
            UserUuidException ex = assertThrows(UserUuidException.class, () -> UserId.of((UUID) null));
            assertEquals("user.uuid.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   ", "\t" })
        @DisplayName("Should throw UserUuidException when String is null or blank")
        void shouldThrowWhenStringIsNullOrBlank(String input) {
            UserUuidException ex = assertThrows(UserUuidException.class, () -> UserId.of(input));
            assertEquals("user.uuid.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw UserUuidException when String has invalid UUID format")
        void shouldThrowWhenStringFormatIsInvalid() {
            UserUuidException ex = assertThrows(UserUuidException.class, () -> UserId.of("not-a-valid-uuid"));
            assertEquals("user.uuid.invalid.format", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("UserName Value Object")
    class UserNameTests {

        @Test
        @DisplayName("Should create UserName and trim whitespace")
        void shouldCreateValidUserName() {
            UserName name = UserName.of("  Rafael Perez  ");
            assertEquals("Rafael Perez", name.value());
            assertEquals("Rafael Perez", name.toString());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   ", "\n" })
        @DisplayName("Should throw UserNameException when name is null or blank")
        void shouldThrowWhenNullOrBlank(String input) {
            UserNameException ex = assertThrows(UserNameException.class, () -> UserName.of(input));
            assertEquals("user.name.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw UserNameException when name is shorter than minimum length (2)")
        void shouldThrowWhenTooShort() {
            UserNameException ex = assertThrows(UserNameException.class, () -> UserName.of("A"));
            assertEquals("user.name.too.short", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw UserNameException when name exceeds maximum length (100)")
        void shouldThrowWhenTooLong() {
            String longName = "a".repeat(101);
            UserNameException ex = assertThrows(UserNameException.class, () -> UserName.of(longName));
            assertEquals("user.name.too.long", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("UserEmail Value Object")
    class UserEmailTests {

        @Test
        @DisplayName("Should create UserEmail normalizing to lowercase and trimming")
        void shouldCreateValidUserEmail() {
            UserEmail email = UserEmail.of("  Rafael.Test@Example.COM  ");
            assertEquals("rafael.test@example.com", email.value());
            assertEquals("rafael.test@example.com", email.toString());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   " })
        @DisplayName("Should throw UserEmailException when email is null or blank")
        void shouldThrowWhenEmailIsNullOrBlank(String input) {
            UserEmailException ex = assertThrows(UserEmailException.class, () -> UserEmail.of(input));
            assertEquals("user.email.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw UserEmailException when email exceeds 255 characters")
        void shouldThrowWhenEmailTooLong() {
            String longLocalPart = "a".repeat(250);
            String longEmail = longLocalPart + "@example.com";
            UserEmailException ex = assertThrows(UserEmailException.class, () -> UserEmail.of(longEmail));
            assertEquals("user.email.too.long", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @ValueSource(strings = { "plainaddress", "@missinglocal.com", "user@.com", "user@domain", "user@domain.c" })
        @DisplayName("Should throw UserEmailException when email format is invalid")
        void shouldThrowWhenEmailInvalidFormat(String invalidEmail) {
            UserEmailException ex = assertThrows(UserEmailException.class, () -> UserEmail.of(invalidEmail));
            assertEquals("user.email.invalid.format", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("UserPassword Value Object")
    class UserPasswordTests {

        @Test
        @DisplayName("Should create UserPassword from valid raw password and mask toString")
        void shouldCreateFromValidRawPassword() {
            UserPassword password = UserPassword.fromRaw("Secret123");
            assertEquals("Secret123", password.value());
            assertEquals("******", password.toString());
        }

        @Test
        @DisplayName("Should create UserPassword from valid hash")
        void shouldCreateFromValidHash() {
            UserPassword password = UserPassword.fromHash("$2a$10$hashedPasswordValue");
            assertEquals("$2a$10$hashedPasswordValue", password.value());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   " })
        @DisplayName("Should throw UserPasswordException when raw password is null or blank")
        void shouldThrowWhenRawPasswordIsNullOrBlank(String input) {
            UserPasswordException ex = assertThrows(UserPasswordException.class, () -> UserPassword.fromRaw(input));
            assertEquals("user.password.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw UserPasswordException when raw password has fewer than 8 characters")
        void shouldThrowWhenRawPasswordIsTooShort() {
            UserPasswordException ex = assertThrows(UserPasswordException.class, () -> UserPassword.fromRaw("1234567"));
            assertEquals("user.password.too.short", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   " })
        @DisplayName("Should throw UserPasswordException when hash is null or blank")
        void shouldThrowWhenHashIsNullOrBlank(String input) {
            UserPasswordException ex = assertThrows(UserPasswordException.class, () -> UserPassword.fromHash(input));
            assertEquals("user.password.invalid.hash", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("UserRole Value Object")
    class UserRoleTests {

        @Test
        @DisplayName("Should create default user and admin roles")
        void shouldCreateDefaultRoles() {
            assertEquals(UserRoleEnum.ROLE_USER, UserRole.user().value());
            assertEquals(UserRoleEnum.ROLE_ADMIN, UserRole.admin().value());
            assertEquals("ROLE_ADMIN", UserRole.admin().toString());
        }

        @Test
        @DisplayName("Should parse role from string case-insensitively")
        void shouldParseRoleFromString() {
            UserRole role = UserRole.of("  role_admin  ");
            assertEquals(UserRoleEnum.ROLE_ADMIN, role.value());
        }

        @Test
        @DisplayName("Should throw UserRoleException when enum is null")
        void shouldThrowWhenEnumIsNull() {
            UserRoleException ex = assertThrows(UserRoleException.class, () -> new UserRole(null));
            assertEquals("user.role.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   " })
        @DisplayName("Should throw UserRoleException when role string is null or blank")
        void shouldThrowWhenRoleStringIsNullOrBlank(String input) {
            UserRoleException ex = assertThrows(UserRoleException.class, () -> UserRole.of(input));
            assertEquals("user.role.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw UserRoleException when role string is unsupported")
        void shouldThrowWhenRoleIsInvalid() {
            UserRoleException ex = assertThrows(UserRoleException.class, () -> UserRole.of("ROLE_SUPERUSER"));
            assertEquals("user.role.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("UserAccount Value Object")
    class UserAccountTests {

        @Test
        @DisplayName("Should create active and inactive accounts and transition state")
        void shouldTransitionAccountState() {
            UserAccount active = UserAccount.active();
            assertTrue(active.isActive());

            UserAccount deactivated = active.deactivate();
            assertFalse(deactivated.isActive());

            UserAccount reactivated = deactivated.activate();
            assertTrue(reactivated.isActive());
        }

        @Test
        @DisplayName("Should throw UserAccountException when activating an already active account")
        void shouldThrowWhenAlreadyActive() {
            UserAccount active = UserAccount.active();
            UserAccountException ex = assertThrows(UserAccountException.class, active::activate);
            assertEquals("user.account.already.active", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw UserAccountException when deactivating an already inactive account")
        void shouldThrowWhenAlreadyInactive() {
            UserAccount inactive = UserAccount.inactive();
            UserAccountException ex = assertThrows(UserAccountException.class, inactive::deactivate);
            assertEquals("user.account.already.inactive", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }
}

