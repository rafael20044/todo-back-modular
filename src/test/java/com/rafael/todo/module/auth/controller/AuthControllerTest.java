package com.rafael.todo.module.auth.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.rafael.todo.module.auth.exception.AuthCredentialsException;
import com.rafael.todo.module.auth.exception.RefreshTokenException;
import com.rafael.todo.module.auth.model.RefreshTokenModel;
import com.rafael.todo.module.auth.model.vo.RefreshTokenExpiration;
import com.rafael.todo.module.auth.model.vo.RefreshTokenHash;
import com.rafael.todo.module.auth.model.vo.RefreshTokenId;
import com.rafael.todo.module.auth.model.vo.RefreshTokenRevoked;
import com.rafael.todo.module.auth.model.vo.RefreshTokenUserId;
import com.rafael.todo.module.auth.service.IFindRefreshTokenUseCase;
import com.rafael.todo.module.auth.service.ILoginUseCase;
import com.rafael.todo.module.auth.service.ILogoutUseCase;
import com.rafael.todo.module.auth.service.IRefreshTokenUseCase;
import com.rafael.todo.module.auth.service.dto.AuthTokensDtoService;
import com.rafael.todo.module.auth.service.dto.LoginDtoService;
import com.rafael.todo.module.auth.service.dto.RefreshTokenRequestDtoService;
import com.rafael.todo.shared.GlobalExceptionController;

import jakarta.servlet.http.Cookie;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController - REST Endpoints, HttpOnly Cookies & Exception Handling Tests")
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ILoginUseCase loginUseCase;

    @Mock
    private IRefreshTokenUseCase refreshTokenUseCase;

    @Mock
    private ILogoutUseCase logoutUseCase;

    @Mock
    private IFindRefreshTokenUseCase findRefreshTokenUseCase;

    @InjectMocks
    private AuthController authController;

    private UUID userId;
    private RefreshTokenModel sampleTokenModel;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionController())
                .build();

        userId = UUID.randomUUID();
        sampleTokenModel = RefreshTokenModel.reconstitute(
                RefreshTokenId.of(1L),
                RefreshTokenUserId.of(userId),
                RefreshTokenHash.of("refresh-token-123"),
                RefreshTokenExpiration.of(LocalDateTime.now().plusDays(7)),
                RefreshTokenRevoked.active(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("POST /auth/login - Should return 200 OK with accessToken in JSON and HttpOnly refreshToken cookie")
    void shouldLoginAndSetHttpOnlyCookie() throws Exception {
        var authTokens = new AuthTokensDtoService(
                "jwt-access-token",
                "refresh-token-123",
                LocalDateTime.now().plusDays(7)
        );
        when(loginUseCase.login(any(LoginDtoService.class))).thenReturn(authTokens);

        String jsonBody = """
                {
                  "email": "rafael@gmail.com",
                  "password": "Password123"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("token=refresh-token-123")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("SameSite=Strict")))
                .andExpect(jsonPath("$.accessToken").value("jwt-access-token"));
    }

    @Test
    @DisplayName("POST /auth/login - Should return 401 Unauthorized when credentials are invalid")
    void shouldReturn401OnInvalidCredentials() throws Exception {
        when(loginUseCase.login(any(LoginDtoService.class)))
                .thenThrow(AuthCredentialsException.invalidCredentials());

        String jsonBody = """
                {
                  "email": "rafael@gmail.com",
                  "password": "WrongPassword"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("auth.credentials.invalid"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /auth/refresh - Should read 'token' cookie and return rotated cookie + new accessToken")
    void shouldRefreshTokenFromCookie() throws Exception {
        var rotatedTokens = new AuthTokensDtoService(
                "new-jwt-access-token",
                "rotated-refresh-token",
                LocalDateTime.now().plusDays(7)
        );
        when(refreshTokenUseCase.refresh(any(RefreshTokenRequestDtoService.class))).thenReturn(rotatedTokens);

        mockMvc.perform(post("/auth/refresh")
                        .cookie(new Cookie("token", "old-refresh-token")))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("token=rotated-refresh-token")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                .andExpect(jsonPath("$.accessToken").value("new-jwt-access-token"));
    }

    @Test
    @DisplayName("POST /auth/logout - Should return 204 No Content and clear 'token' cookie (Max-Age=0)")
    void shouldLogoutAndClearCookie() throws Exception {
        doNothing().when(logoutUseCase).logout("refresh-token-123");

        mockMvc.perform(post("/auth/logout")
                        .cookie(new Cookie("token", "refresh-token-123")))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("token=")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")));

        verify(logoutUseCase).logout("refresh-token-123");
    }

    @Test
    @DisplayName("DELETE /auth/logout/{userId} - Should return 204 No Content")
    void shouldLogoutAllByUserId() throws Exception {
        doNothing().when(logoutUseCase).logoutAllByUserId(userId);

        mockMvc.perform(delete("/auth/logout/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(logoutUseCase).logoutAllByUserId(userId);
    }

    @Test
    @DisplayName("GET /auth/tokens - Should return 200 OK with paginated ResponseRefreshTokenDto")
    void shouldListTokensPaginated() throws Exception {
        when(findRefreshTokenUseCase.findAllFiltered(userId, false)).thenReturn(List.of(sampleTokenModel));

        mockMvc.perform(get("/auth/tokens")
                        .param("userId", userId.toString())
                        .param("isRevoked", "false")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].userId").value(userId.toString()))
                .andExpect(jsonPath("$.content[0].isRevoked").value(false))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /auth/tokens/{id} - Should return 200 OK when found and 404 when not found")
    void shouldFindTokenByIdOrReturn404() throws Exception {
        when(findRefreshTokenUseCase.findById(1L)).thenReturn(sampleTokenModel);
        when(findRefreshTokenUseCase.findById(99L)).thenThrow(RefreshTokenException.notFound());

        mockMvc.perform(get("/auth/tokens/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(userId.toString()));

        mockMvc.perform(get("/auth/tokens/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("auth.refresh.token.not.found"))
                .andExpect(jsonPath("$.status").value(404));
    }
}

