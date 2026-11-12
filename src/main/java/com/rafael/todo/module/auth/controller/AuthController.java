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

import java.util.UUID;

@RestController
@RequestMapping("/auth")
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
    public ResponseEntity<ResponseAuthDto> login(@RequestBody LoginDto dto) {
        var data = this.loginUseCase.login(AuthMapperController.toServiceDto(dto));
        return createResponseWithCookie(data.refreshToken(), data);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseAuthDto> refresh(@CookieValue(name = "token") String refreshToken) {
        var data = this.refreshTokenUseCase.refresh(AuthMapperController.toServiceDto(refreshToken));
        return createResponseWithCookie(data.refreshToken(), data);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "token") String refreshToken) {
        this.logoutUseCase.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/logout/{userId}")
    public ResponseEntity<Void> logoutAllByUserId(@PathVariable UUID userId) {
        this.logoutUseCase.logoutAllByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tokens")
    public ResponseEntity<Page<ResponseRefreshTokenDto>> findAll(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) Boolean isRevoked,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        var list = this.findRefreshTokenUseCase.findAllFiltered(userId, isRevoked);
        return ResponseEntity.ok(AuthMapperController.toPage(list, page, size));
    }

    @GetMapping("/tokens/{id}")
    public ResponseEntity<ResponseRefreshTokenDto> findById(@PathVariable Long id) {
        var model = this.findRefreshTokenUseCase.findById(id);
        return ResponseEntity.ok(AuthMapperController.toResponseDto(model));
    }

    private ResponseEntity<ResponseAuthDto> createResponseWithCookie(String refreshToken, AuthTokensDtoService data) {
        var cookie = ResponseCookie.from(MyConst.COOKIE_NAME.getValue(), refreshToken)
                .httpOnly(true)
                .path("/")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(AuthMapperController.toResponseDto(data));
    }
}
