package com.rafael.todo.module.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafael.todo.module.user.exception.UserEmailException;
import com.rafael.todo.module.user.exception.UserUuidException;
import com.rafael.todo.module.user.model.UserModel;
import com.rafael.todo.module.user.model.enums.UserRoleEnum;
import com.rafael.todo.module.user.model.vo.UserEmail;
import com.rafael.todo.module.user.model.vo.UserName;
import com.rafael.todo.module.user.model.vo.UserPassword;
import com.rafael.todo.module.user.repository.adapter.ICreateUserAdapter;
import com.rafael.todo.module.user.repository.adapter.IDeleteUserAdapter;
import com.rafael.todo.module.user.repository.adapter.IFindUserAdapter;
import com.rafael.todo.module.user.repository.adapter.IUpdateUserAdapter;
import com.rafael.todo.module.user.service.dto.CreateUserDtoService;
import com.rafael.todo.module.user.service.dto.UpdateUserDtoService;
import com.rafael.todo.module.user.service.imp.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Use Cases Unit Tests")
class UserServiceTest {

    @Mock
    private ICreateUserAdapter createUserAdapter;

    @Mock
    private IUpdateUserAdapter updateUserAdapter;

    @Mock
    private IDeleteUserAdapter deleteUserAdapter;

    @Mock
    private IFindUserAdapter findUserAdapter;

    @InjectMocks
    private UserService userService;

    private UserModel sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = UserModel.create(
                UserName.of("Rafael"),
                UserEmail.of("rafael@example.com"),
                UserPassword.fromRaw("Password123")
        );
    }

    @Nested
    @DisplayName("Create User Use Case")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user when email is not registered")
        void shouldCreateUserSuccessfully() {
            CreateUserDtoService dto = new CreateUserDtoService(
                    "Rafael",
                    "rafael@example.com",
                    "Password123",
                    "ROLE_USER"
            );

            when(findUserAdapter.findByEmail("rafael@example.com")).thenReturn(Optional.empty());
            when(createUserAdapter.create(any(UserModel.class))).thenAnswer(inv -> inv.getArgument(0));

            UserModel result = userService.create(dto);

            assertNotNull(result);
            assertEquals("Rafael", result.getNameValue());
            assertEquals("rafael@example.com", result.getEmailValue());
            assertEquals(UserRoleEnum.ROLE_USER, result.getRoleValue());
            verify(findUserAdapter).findByEmail("rafael@example.com");
            verify(createUserAdapter).create(any(UserModel.class));
        }

        @Test
        @DisplayName("Should throw UserEmailException (409) when email already exists")
        void shouldThrowWhenEmailAlreadyExists() {
            CreateUserDtoService dto = new CreateUserDtoService(
                    "Rafael",
                    "rafael@example.com",
                    "Password123",
                    null
            );

            when(findUserAdapter.findByEmail("rafael@example.com")).thenReturn(Optional.of(sampleUser));

            UserEmailException ex = assertThrows(UserEmailException.class, () -> userService.create(dto));

            assertEquals("user.email.already.exists", ex.getCode());
            assertEquals(409, ex.getStatusCode());
            verify(createUserAdapter, never()).create(any());
        }
    }

    @Nested
    @DisplayName("Find User Use Case")
    class FindUserTests {

        @Test
        @DisplayName("Should return UserModel when UUID exists")
        void shouldReturnUserById() {
            UUID id = sampleUser.getIdValue();
            when(findUserAdapter.findById(id)).thenReturn(Optional.of(sampleUser));

            UserModel result = userService.findById(id);

            assertEquals(id, result.getIdValue());
            assertEquals("Rafael", result.getNameValue());
            verify(findUserAdapter).findById(id);
        }

        @Test
        @DisplayName("Should throw UserUuidException (404) when UUID does not exist")
        void shouldThrowWhenUserNotFound() {
            UUID id = UUID.randomUUID();
            when(findUserAdapter.findById(id)).thenReturn(Optional.empty());

            UserUuidException ex = assertThrows(UserUuidException.class, () -> userService.findById(id));

            assertEquals("user.uuid.not.found", ex.getCode());
            assertEquals(404, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should return all users")
        void shouldReturnAllUsers() {
            when(findUserAdapter.findAll()).thenReturn(List.of(sampleUser));

            List<UserModel> result = userService.findAll();

            assertEquals(1, result.size());
            assertEquals(sampleUser.getIdValue(), result.getFirst().getIdValue());
            verify(findUserAdapter).findAll();
        }
    }

    @Nested
    @DisplayName("Update User Use Case")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update name, password, and role with updatePassword=true when password is provided")
        void shouldUpdateAllFieldsIncludingPassword() {
            UUID id = sampleUser.getIdValue();
            UpdateUserDtoService updateDto = new UpdateUserDtoService(
                    id,
                    "Rafael Updated",
                    "NewSecret123",
                    "ROLE_ADMIN"
            );

            when(findUserAdapter.findById(id)).thenReturn(Optional.of(sampleUser));
            when(updateUserAdapter.update(any(UserModel.class), eq(true))).thenAnswer(inv -> inv.getArgument(0));

            UserModel updated = userService.update(updateDto);

            assertEquals("Rafael Updated", updated.getNameValue());
            assertEquals("NewSecret123", updated.getPasswordValue());
            assertEquals(UserRoleEnum.ROLE_ADMIN, updated.getRoleValue());

            ArgumentCaptor<UserModel> modelCaptor = ArgumentCaptor.forClass(UserModel.class);
            verify(updateUserAdapter).update(modelCaptor.capture(), eq(true));
            assertEquals("Rafael Updated", modelCaptor.getValue().getNameValue());
        }

        @Test
        @DisplayName("Should preserve existing fields and pass updatePassword=false when password is null")
        void shouldPreserveFieldsWhenNullInUpdateDto() {
            UUID id = sampleUser.getIdValue();
            UpdateUserDtoService partialUpdateDto = new UpdateUserDtoService(
                    id,
                    null,
                    null,
                    null
            );

            when(findUserAdapter.findById(id)).thenReturn(Optional.of(sampleUser));
            when(updateUserAdapter.update(any(UserModel.class), eq(false))).thenAnswer(inv -> inv.getArgument(0));

            UserModel updated = userService.update(partialUpdateDto);

            assertEquals(sampleUser.getNameValue(), updated.getNameValue());
            assertEquals(sampleUser.getPasswordValue(), updated.getPasswordValue());
            assertEquals(sampleUser.getRoleValue(), updated.getRoleValue());
            verify(updateUserAdapter).update(any(UserModel.class), eq(false));
        }

        @Test
        @DisplayName("Should throw UserUuidException (404) when updating non-existent user")
        void shouldThrowWhenUpdatingNonExistentUser() {
            UUID id = UUID.randomUUID();
            UpdateUserDtoService updateDto = new UpdateUserDtoService(id, "New Name", null, null);

            when(findUserAdapter.findById(id)).thenReturn(Optional.empty());

            UserUuidException ex = assertThrows(UserUuidException.class, () -> userService.update(updateDto));
            assertEquals(404, ex.getStatusCode());
            verify(updateUserAdapter, never()).update(any(), any(Boolean.class));
        }
    }

    @Nested
    @DisplayName("Delete User Use Case")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delegate user deletion by UUID to deleteUserAdapter")
        void shouldDeleteUserByUuid() {
            UUID id = sampleUser.getIdValue();

            userService.delete(id);

            verify(deleteUserAdapter).delete(id);
        }
    }
}

