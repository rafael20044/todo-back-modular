package com.rafael.todo.module.user.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.rafael.todo.module.user.model.UserModel;
import com.rafael.todo.module.user.model.vo.UserEmail;
import com.rafael.todo.module.user.model.vo.UserName;
import com.rafael.todo.module.user.model.vo.UserPassword;
import com.rafael.todo.module.user.repository.adapter.imp.UserAdapter;
import com.rafael.todo.module.user.repository.entity.UserEntity;
import com.rafael.todo.module.user.repository.mapper.UserMapperRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAdapter - Persistence Adapter Unit Tests")
class UserAdapterTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAdapter userAdapter;

    private UserModel sampleModel;

    @BeforeEach
    void setUp() {
        sampleModel = UserModel.create(
                UserName.of("Rafael"),
                UserEmail.of("rafael@example.com"),
                UserPassword.fromRaw("RawPassword123")
        );
    }

    @Test
    @DisplayName("Should encode password before saving new user entity in create")
    void shouldEncodePasswordAndSaveOnCreate() {
        when(passwordEncoder.encode("RawPassword123")).thenReturn("$2a$10$encodedHash");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserModel saved = userAdapter.create(sampleModel);

        ArgumentCaptor<UserEntity> entityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(passwordEncoder).encode("RawPassword123");
        verify(userRepository).save(entityCaptor.capture());

        assertEquals("$2a$10$encodedHash", entityCaptor.getValue().getPasswordHash());
        assertEquals("$2a$10$encodedHash", saved.getPasswordValue());
        assertEquals(sampleModel.getIdValue(), saved.getIdValue());
    }

    @Test
    @DisplayName("Should re-encode password on update when updatePassword is true")
    void shouldEncodePasswordOnUpdateWhenFlagIsTrue() {
        when(passwordEncoder.encode("RawPassword123")).thenReturn("$2a$10$newEncodedHash");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserModel updated = userAdapter.update(sampleModel, true);

        verify(passwordEncoder).encode("RawPassword123");
        assertEquals("$2a$10$newEncodedHash", updated.getPasswordValue());
    }

    @Test
    @DisplayName("Should NOT re-encode password on update when updatePassword is false")
    void shouldNotEncodePasswordOnUpdateWhenFlagIsFalse() {
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserModel updated = userAdapter.update(sampleModel, false);

        verify(passwordEncoder, never()).encode(any());
        assertEquals("RawPassword123", updated.getPasswordValue());
    }

    @Test
    @DisplayName("Should find user by ID and map to UserModel")
    void shouldFindById() {
        UUID id = sampleModel.getIdValue();
        UserEntity entity = UserMapperRepository.toEntity(sampleModel);
        when(userRepository.findById(id)).thenReturn(Optional.of(entity));

        Optional<UserModel> found = userAdapter.findById(id);

        assertTrue(found.isPresent());
        assertEquals(id, found.get().getIdValue());
    }

    @Test
    @DisplayName("Should return empty Optional when user ID does not exist")
    void shouldReturnEmptyWhenIdNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Optional<UserModel> found = userAdapter.findById(id);

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find user by email and map to UserModel")
    void shouldFindByEmail() {
        UserEntity entity = UserMapperRepository.toEntity(sampleModel);
        when(userRepository.findByEmail("rafael@example.com")).thenReturn(Optional.of(entity));

        Optional<UserModel> found = userAdapter.findByEmail("rafael@example.com");

        assertTrue(found.isPresent());
        assertEquals("rafael@example.com", found.get().getEmailValue());
    }

    @Test
    @DisplayName("Should find all users and map to list of UserModel")
    void shouldFindAllUsers() {
        UserEntity entity = UserMapperRepository.toEntity(sampleModel);
        when(userRepository.findAll()).thenReturn(List.of(entity));

        List<UserModel> users = userAdapter.findAll();

        assertEquals(1, users.size());
        assertEquals(sampleModel.getIdValue(), users.getFirst().getIdValue());
    }

    @Test
    @DisplayName("Should delete user by ID in repository")
    void shouldDeleteById() {
        UUID id = sampleModel.getIdValue();

        userAdapter.delete(id);

        verify(userRepository).deleteById(id);
    }
}

