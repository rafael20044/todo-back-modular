package com.rafael.todo.module.user.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rafael.todo.module.user.model.enums.UserRoleEnum;
import com.rafael.todo.module.user.model.vo.UserAccount;
import com.rafael.todo.module.user.model.vo.UserEmail;
import com.rafael.todo.module.user.model.vo.UserId;
import com.rafael.todo.module.user.model.vo.UserName;
import com.rafael.todo.module.user.model.vo.UserPassword;
import com.rafael.todo.module.user.model.vo.UserRole;

@DisplayName("UserModel - Domain Model Unit Tests")
class UserModelTest {

    @Test
    @DisplayName("Should create new UserModel with default ROLE_USER and active account")
    void shouldCreateNewUserModelWithDefaultRole() {
        UserModel user = UserModel.create(
                UserName.of("Rafael"),
                UserEmail.of("rafael@example.com"),
                UserPassword.fromRaw("Password123")
        );

        assertNotNull(user.getIdValue());
        assertEquals("Rafael", user.getNameValue());
        assertEquals("rafael@example.com", user.getEmailValue());
        assertEquals("Password123", user.getPasswordValue());
        assertEquals(UserRoleEnum.ROLE_USER, user.getRoleValue());
        assertTrue(user.getAccountValue());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create new UserModel with custom role")
    void shouldCreateNewUserModelWithCustomRole() {
        UserModel admin = UserModel.create(
                UserName.of("Admin"),
                UserEmail.of("admin@example.com"),
                UserPassword.fromRaw("AdminPass123"),
                UserRole.admin()
        );

        assertEquals(UserRoleEnum.ROLE_ADMIN, admin.getRoleValue());
        assertTrue(admin.getAccountValue());
    }

    @Test
    @DisplayName("Should reconstitute UserModel preserving ID and timestamps")
    void shouldReconstituteUserModel() {
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 10, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 1, 15, 12, 30);

        UserModel reconstituted = UserModel.reconstitute(
                UserId.of(id),
                UserEmail.of("user@example.com"),
                UserPassword.fromHash("$2a$10$hash"),
                UserName.of("Existing User"),
                UserRole.admin(),
                UserAccount.inactive(),
                createdAt,
                updatedAt
        );

        assertEquals(id, reconstituted.getIdValue());
        assertEquals("user@example.com", reconstituted.getEmailValue());
        assertEquals("$2a$10$hash", reconstituted.getPasswordValue());
        assertEquals("Existing User", reconstituted.getNameValue());
        assertEquals(UserRoleEnum.ROLE_ADMIN, reconstituted.getRoleValue());
        assertFalse(reconstituted.getAccountValue());
        assertEquals(createdAt, reconstituted.getCreatedAt());
        assertEquals(updatedAt, reconstituted.getUpdatedAt());
    }

    @Test
    @DisplayName("Should perform immutable copy-on-write mutations preserving original instance")
    void shouldPerformImmutableMutations() {
        UserModel original = UserModel.create(
                UserName.of("Original Name"),
                UserEmail.of("original@example.com"),
                UserPassword.fromRaw("Password123")
        );

        UserModel renamed = original.changeName(UserName.of("Updated Name"));
        assertNotSame(original, renamed);
        assertEquals("Original Name", original.getNameValue());
        assertEquals("Updated Name", renamed.getNameValue());
        assertEquals(original.getIdValue(), renamed.getIdValue());
        assertEquals(original.getCreatedAt(), renamed.getCreatedAt());

        UserModel passwordChanged = renamed.changePassword(UserPassword.fromRaw("NewSecret999"));
        assertEquals("NewSecret999", passwordChanged.getPasswordValue());

        UserModel roleChanged = passwordChanged.changeRole(UserRole.admin());
        assertEquals(UserRoleEnum.ROLE_ADMIN, roleChanged.getRoleValue());

        UserModel deactivated = roleChanged.deactivate();
        assertFalse(deactivated.getAccountValue());

        UserModel reactivated = deactivated.activate();
        assertTrue(reactivated.getAccountValue());
    }
}

