package com.rafael.todo.module.auth.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.rafael.todo.module.auth.exception.AuthCredentialsException;
import com.rafael.todo.module.auth.exception.RefreshTokenException;
import com.rafael.todo.module.auth.model.RefreshTokenModel;
import com.rafael.todo.module.auth.model.vo.RefreshTokenExpiration;
import com.rafael.todo.module.auth.model.vo.RefreshTokenHash;
import com.rafael.todo.module.auth.model.vo.RefreshTokenId;
import com.rafael.todo.module.auth.model.vo.RefreshTokenRevoked;
import com.rafael.todo.module.auth.model.vo.RefreshTokenUserId;
import com.rafael.todo.module.auth.repository.adapter.ICreateRefreshTokenAdapter;
import com.rafael.todo.module.auth.repository.adapter.IDeleteRefreshTokenAdapter;
import com.rafael.todo.module.auth.repository.adapter.IFindRefreshTokenAdapter;
import com.rafael.todo.module.auth.repository.adapter.IUpdateRefreshTokenAdapter;
import com.rafael.todo.module.auth.service.dto.LoginDtoService;
import com.rafael.todo.module.auth.service.dto.RefreshTokenRequestDtoService;
import com.rafael.todo.module.auth.service.imp.AuthService;
import com.rafael.todo.module.user.model.UserModel;
import com.rafael.todo.module.user.model.vo.UserEmail;
import com.rafael.todo.module.user.model.vo.UserName;
import com.rafael.todo.module.user.model.vo.UserPassword;
import com.rafael.todo.module.user.service.IFindUserUseCase;
import com.rafael.todo.shared.IJwtService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Business Logic & Inter-Module Validation Tests")
class AuthServiceTest {

    @Mock
    private ICreateRefreshTokenAdapter createRefreshTokenAdapter;

    @Mock
    private IFindRefreshTokenAdapter findRefreshTokenAdapter;

    @Mock
    private IUpdateRefreshTokenAdapter updateRefreshTokenAdapter;

    @Mock
    private IDeleteRefreshTokenAdapter deleteRefreshTokenAdapter;

    @Mock
    private IFindUserUseCase findUserUseCase;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IJwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private UserModel activeUser;

    @BeforeEach
    void setUp() {
        activeUser = UserModel.create(
                UserName.of("Rafael"),
                UserEmail.of("rafael@gmail.com"),
                UserPassword.fromHash("$2a$10$hashedPassword")
        );
    }

    @Test
    @DisplayName("login - Should authenticate user, persist RefreshTokenModel and return tokens")
    void shouldLoginSuccessfully() {
        var loginDto = new LoginDtoService("rafael@gmail.com", "PlainPass123");
        var expiresAt = LocalDateTime.now().plusDays(7);

        when(findUserUseCase.findByEmail("rafael@gmail.com")).thenReturn(activeUser);
        when(passwordEncoder.matches("PlainPass123", "$2a$10$hashedPassword")).thenReturn(true);
        when(jwtService.generateAccessToken(activeUser.getIdValue(), "rafael@gmail.com", "ROLE_USER"))
                .thenReturn("jwt-access-token");
        when(jwtService.generateRefreshToken()).thenReturn("raw-refresh-token");
        when(jwtService.calculateRefreshTokenExpiration()).thenReturn(expiresAt);

        var result = authService.login(loginDto);

        assertNotNull(result);
        assertEquals("jwt-access-token", result.accessToken());
        assertEquals("raw-refresh-token", result.refreshToken());
        assertEquals(expiresAt, result.expiresAt());

        var captor = ArgumentCaptor.forClass(RefreshTokenModel.class);
        verify(createRefreshTokenAdapter).create(captor.capture());
        assertEquals(activeUser.getIdValue(), captor.getValue().getUserIdValue());
        assertEquals("raw-refresh-token", captor.getValue().getTokenHashValue());
    }

    @Test
    @DisplayName("login - Should throw 403 AuthCredentialsException when user account is disabled")
    void shouldThrow403WhenAccountDisabledOnLogin() {
        var disabledUser = activeUser.deactivate();
        when(findUserUseCase.findByEmail("rafael@gmail.com")).thenReturn(disabledUser);

        var ex = assertThrows(AuthCredentialsException.class,
                () -> authService.login(new LoginDtoService("rafael@gmail.com", "PlainPass123")));

        assertEquals("auth.account.disabled", ex.getCode());
        assertEquals(403, ex.getStatusCode());
        verify(createRefreshTokenAdapter, never()).create(any());
    }

    @Test
    @DisplayName("login - Should throw 401 AuthCredentialsException when password does not match")
    void shouldThrow401WhenInvalidPassword() {
        when(findUserUseCase.findByEmail("rafael@gmail.com")).thenReturn(activeUser);
        when(passwordEncoder.matches("WrongPassword", "$2a$10$hashedPassword")).thenReturn(false);

        var ex = assertThrows(AuthCredentialsException.class,
                () -> authService.login(new LoginDtoService("rafael@gmail.com", "WrongPassword")));

        assertEquals("auth.credentials.invalid", ex.getCode());
        assertEquals(401, ex.getStatusCode());
        verify(createRefreshTokenAdapter, never()).create(any());
    }

    @Test
    @DisplayName("refresh - Should revoke old token, generate new tokens and persist rotated token")
    void shouldRefreshTokensSuccessfully() {
        var existingToken = RefreshTokenModel.reconstitute(
                RefreshTokenId.of(10L),
                RefreshTokenUserId.of(activeUser.getIdValue()),
                RefreshTokenHash.of("old-refresh-token"),
                RefreshTokenExpiration.of(LocalDateTime.now().plusDays(5)),
                RefreshTokenRevoked.active(),
                LocalDateTime.now().minusDays(2)
        );
        var newExpiresAt = LocalDateTime.now().plusDays(7);

        when(findRefreshTokenAdapter.findByTokenHash("old-refresh-token")).thenReturn(Optional.of(existingToken));
        when(findUserUseCase.findById(activeUser.getIdValue())).thenReturn(activeUser);
        when(jwtService.generateAccessToken(activeUser.getIdValue(), "rafael@gmail.com", "ROLE_USER"))
                .thenReturn("new-access-token");
        when(jwtService.generateRefreshToken()).thenReturn("new-refresh-token");
        when(jwtService.calculateRefreshTokenExpiration()).thenReturn(newExpiresAt);

        var result = authService.refresh(new RefreshTokenRequestDtoService("old-refresh-token"));

        assertEquals("new-access-token", result.accessToken());
        assertEquals("new-refresh-token", result.refreshToken());
        assertEquals(newExpiresAt, result.expiresAt());

        var updateCaptor = ArgumentCaptor.forClass(RefreshTokenModel.class);
        verify(updateRefreshTokenAdapter).update(updateCaptor.capture());
        assertTrue(updateCaptor.getValue().isRevokedValue());

        verify(createRefreshTokenAdapter).create(any(RefreshTokenModel.class));
    }

    @Test
    @DisplayName("refresh - Should throw 401 RefreshTokenException when token is already revoked")
    void shouldThrow401WhenRefreshTokenRevoked() {
        var revokedToken = RefreshTokenModel.reconstitute(
                RefreshTokenId.of(10L),
                RefreshTokenUserId.of(activeUser.getIdValue()),
                RefreshTokenHash.of("revoked-token"),
                RefreshTokenExpiration.of(LocalDateTime.now().plusDays(5)),
                RefreshTokenRevoked.revoked(),
                LocalDateTime.now().minusDays(2)
        );
        when(findRefreshTokenAdapter.findByTokenHash("revoked-token")).thenReturn(Optional.of(revokedToken));

        var ex = assertThrows(RefreshTokenException.class,
                () -> authService.refresh(new RefreshTokenRequestDtoService("revoked-token")));

        assertEquals("auth.refresh.token.revoked", ex.getCode());
        assertEquals(401, ex.getStatusCode());
    }

    @Test
    @DisplayName("refresh - Should throw 401 RefreshTokenException when token is expired")
    void shouldThrow401WhenRefreshTokenExpired() {
        var expiredToken = RefreshTokenModel.reconstitute(
                RefreshTokenId.of(10L),
                RefreshTokenUserId.of(activeUser.getIdValue()),
                RefreshTokenHash.of("expired-token"),
                RefreshTokenExpiration.of(LocalDateTime.now().minusHours(1)),
                RefreshTokenRevoked.active(),
                LocalDateTime.now().minusDays(8)
        );
        when(findRefreshTokenAdapter.findByTokenHash("expired-token")).thenReturn(Optional.of(expiredToken));

        var ex = assertThrows(RefreshTokenException.class,
                () -> authService.refresh(new RefreshTokenRequestDtoService("expired-token")));

        assertEquals("auth.refresh.token.expired", ex.getCode());
        assertEquals(401, ex.getStatusCode());
    }

    @Test
    @DisplayName("refresh - Should throw 403 AuthCredentialsException when user account is disabled")
    void shouldThrow403WhenUserDisabledDuringRefresh() {
        var existingToken = RefreshTokenModel.reconstitute(
                RefreshTokenId.of(10L),
                RefreshTokenUserId.of(activeUser.getIdValue()),
                RefreshTokenHash.of("valid-token"),
                RefreshTokenExpiration.of(LocalDateTime.now().plusDays(5)),
                RefreshTokenRevoked.active(),
                LocalDateTime.now()
        );
        when(findRefreshTokenAdapter.findByTokenHash("valid-token")).thenReturn(Optional.of(existingToken));
        when(findUserUseCase.findById(activeUser.getIdValue())).thenReturn(activeUser.deactivate());

        var ex = assertThrows(AuthCredentialsException.class,
                () -> authService.refresh(new RefreshTokenRequestDtoService("valid-token")));

        assertEquals("auth.account.disabled", ex.getCode());
        assertEquals(403, ex.getStatusCode());
    }

    @Test
    @DisplayName("logout & logoutAllByUserId - Should delete refresh token(s)")
    void shouldLogoutAndLogoutAllByUserId() {
        var existingToken = RefreshTokenModel.reconstitute(
                RefreshTokenId.of(25L),
                RefreshTokenUserId.of(activeUser.getIdValue()),
                RefreshTokenHash.of("logout-token"),
                RefreshTokenExpiration.of(LocalDateTime.now().plusDays(5)),
                RefreshTokenRevoked.active(),
                LocalDateTime.now()
        );
        when(findRefreshTokenAdapter.findByTokenHash("logout-token")).thenReturn(Optional.of(existingToken));

        authService.logout("logout-token");
        verify(deleteRefreshTokenAdapter).delete(25L);

        authService.logoutAllByUserId(activeUser.getIdValue());
        verify(deleteRefreshTokenAdapter).deleteByUserId(activeUser.getIdValue());
    }

    @Test
    @DisplayName("findById & findAllFiltered - Should return models or throw 404 when not found")
    void shouldFindTokensOrThrow404() {
        var token = RefreshTokenModel.reconstitute(
                RefreshTokenId.of(1L),
                RefreshTokenUserId.of(activeUser.getIdValue()),
                RefreshTokenHash.of("token-1"),
                RefreshTokenExpiration.of(LocalDateTime.now().plusDays(7)),
                RefreshTokenRevoked.active(),
                LocalDateTime.now()
        );
        when(findRefreshTokenAdapter.findById(1L)).thenReturn(Optional.of(token));
        when(findRefreshTokenAdapter.findById(999L)).thenReturn(Optional.empty());
        when(findRefreshTokenAdapter.findAllFiltered(activeUser.getIdValue(), false)).thenReturn(List.of(token));

        assertEquals(1L, authService.findById(1L).getIdValue());
        assertEquals(1, authService.findAllFiltered(activeUser.getIdValue(), false).size());

        var ex = assertThrows(RefreshTokenException.class, () -> authService.findById(999L));
        assertEquals("auth.refresh.token.not.found", ex.getCode());
        assertEquals(404, ex.getStatusCode());
    }
}

