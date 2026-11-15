package com.rafael.todo.module.auth.controller;

import com.rafael.todo.module.auth.controller.dto.LoginDto;
import com.rafael.todo.module.auth.controller.dto.ResponseAuthDto;
import com.rafael.todo.module.auth.controller.dto.ResponseRefreshTokenDto;
import com.rafael.todo.module.auth.controller.mapper.AuthMapperController;
import com.rafael.todo.module.auth.service.IFindRefreshTokenUseCase;
import com.rafael.todo.module.auth.service.ILoginUseCase;
import com.rafael.todo.module.auth.service.ILogoutUseCase;
import com.rafael.todo.module.auth.service.IRefreshTokenUseCase;
import com.rafael.todo.module.auth.service.dto.AuthTokensDtoService;
import com.rafael.todo.shared.MyConst;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@Tag(name = "1. Autenticación (Auth)", description = "Inicio de sesión, rotación de Refresh Tokens (cookie HttpOnly 'token') y gestión de sesiones")
@Log
public class AuthController {

    private final ILoginUseCase loginUseCase;
    private final IRefreshTokenUseCase refreshTokenUseCase;
    private final ILogoutUseCase logoutUseCase;
    private final IFindRefreshTokenUseCase findRefreshTokenUseCase;

    @Autowired
    public AuthController(
            ILoginUseCase loginUseCase,
            IRefreshTokenUseCase refreshTokenUseCase,
            ILogoutUseCase logoutUseCase,
            IFindRefreshTokenUseCase findRefreshTokenUseCase) {
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.findRefreshTokenUseCase = findRefreshTokenUseCase;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida credenciales, devuelve el accessToken JWT en el cuerpo JSON y almacena el refreshToken en la cookie HttpOnly 'token'")
    public ResponseEntity<ResponseAuthDto> login(@RequestBody LoginDto dto) {
        log.info("POST /auth/login - Login request received for email: " + dto.email());
        var data = this.loginUseCase.login(AuthMapperController.toServiceDto(dto));
        log.info("POST /auth/login - User authenticated successfully: " + dto.email());
        return createResponseWithCookie(data.refreshToken(), data);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar Access Token", description = "Lee la cookie HttpOnly 'token', revoca el refreshToken anterior, emite uno nuevo en Set-Cookie y devuelve un nuevo accessToken")
    public ResponseEntity<ResponseAuthDto> refresh(@CookieValue(name = "token") String refreshToken) {
        log.info("POST /auth/refresh - Refresh token rotation request received");
        var data = this.refreshTokenUseCase.refresh(AuthMapperController.toServiceDto(refreshToken));
        log.info("POST /auth/refresh - Refresh token rotated successfully");
        return createResponseWithCookie(data.refreshToken(), data);
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión actual", description = "Elimina el refreshToken actual de la base de datos y limpia la cookie HttpOnly 'token' (Max-Age=0)")
    public ResponseEntity<Void> logout(@CookieValue(name = "token") String refreshToken) {
        log.info("POST /auth/logout - Logout request received for current session");
        this.logoutUseCase.logout(refreshToken);
        var clearCookie = ResponseCookie.from(MyConst.COOKIE_NAME.getValue(), "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        log.info("POST /auth/logout - Session closed and refresh cookie cleared");
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .build();
    }

    @DeleteMapping("/logout/{userId}")
    @Operation(summary = "Cerrar todas las sesiones de un usuario", description = "Elimina todos los refresh tokens asociados al UUID del usuario")
    public ResponseEntity<Void> logoutAllByUserId(@PathVariable UUID userId) {
        log.info("DELETE /auth/logout/" + userId + " - Request received to close all sessions for user");
        this.logoutUseCase.logoutAllByUserId(userId);
        log.info("DELETE /auth/logout/" + userId + " - All sessions closed for user");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tokens")
    @Operation(summary = "Listar Refresh Tokens", description = "Obtiene una lista paginada de refresh tokens con filtros opcionales por userId e isRevoked")
    public ResponseEntity<Page<ResponseRefreshTokenDto>> findAll(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) Boolean isRevoked,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info(String.format("GET /auth/tokens - Listing refresh tokens (userId: %s, isRevoked: %s, page: %d, size: %d)", userId, isRevoked, page, size));
        var list = this.findRefreshTokenUseCase.findAllFiltered(userId, isRevoked);
        return ResponseEntity.ok(AuthMapperController.toPage(list, page, size));
    }

    @GetMapping("/tokens/{id}")
    @Operation(summary = "Obtener Refresh Token por ID", description = "Devuelve los metadatos de un refresh token específico por su identificador numérico")
    public ResponseEntity<ResponseRefreshTokenDto> findById(@PathVariable Long id) {
        log.info("GET /auth/tokens/" + id + " - Fetching refresh token by ID");
        var model = this.findRefreshTokenUseCase.findById(id);
        return ResponseEntity.ok(AuthMapperController.toResponseDto(model));
    }

    private ResponseEntity<ResponseAuthDto> createResponseWithCookie(String refreshToken, AuthTokensDtoService data) {
        var maxAge = Duration.between(LocalDateTime.now(), data.expiresAt());
        var cookie = ResponseCookie.from(MyConst.COOKIE_NAME.getValue(), refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(maxAge.isNegative() ? Duration.ZERO : maxAge)
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(AuthMapperController.toResponseDto(data));
    }
}
