package com.rafael.todo.module.auth.service.imp;

import com.rafael.todo.module.auth.exception.AuthCredentialsException;
import com.rafael.todo.module.auth.exception.RefreshTokenException;
import com.rafael.todo.module.auth.model.RefreshTokenModel;
import com.rafael.todo.module.auth.repository.adapter.ICreateRefreshTokenAdapter;
import com.rafael.todo.module.auth.repository.adapter.IDeleteRefreshTokenAdapter;
import com.rafael.todo.module.auth.repository.adapter.IFindRefreshTokenAdapter;
import com.rafael.todo.module.auth.repository.adapter.IUpdateRefreshTokenAdapter;
import com.rafael.todo.module.auth.service.IFindRefreshTokenUseCase;
import com.rafael.todo.module.auth.service.ILoginUseCase;
import com.rafael.todo.module.auth.service.ILogoutUseCase;
import com.rafael.todo.module.auth.service.IRefreshTokenUseCase;
import com.rafael.todo.module.auth.service.dto.AuthTokensDtoService;
import com.rafael.todo.module.auth.service.dto.CreateRefreshTokenDtoService;
import com.rafael.todo.module.auth.service.dto.LoginDtoService;
import com.rafael.todo.module.auth.service.dto.RefreshTokenRequestDtoService;
import com.rafael.todo.module.auth.service.mapper.RefreshTokenMapperService;
import com.rafael.todo.module.user.service.IFindUserUseCase;
import com.rafael.todo.shared.IJwtService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Log
public class AuthService implements
        ILoginUseCase,
        IRefreshTokenUseCase,
        ILogoutUseCase,
        IFindRefreshTokenUseCase {

    private final ICreateRefreshTokenAdapter createRefreshTokenAdapter;
    private final IFindRefreshTokenAdapter findRefreshTokenAdapter;
    private final IUpdateRefreshTokenAdapter updateRefreshTokenAdapter;
    private final IDeleteRefreshTokenAdapter deleteRefreshTokenAdapter;
    private final IFindUserUseCase findUserUseCase;
    private final PasswordEncoder passwordEncoder;
    private final IJwtService jwtService;

    @Autowired
    public AuthService(
            ICreateRefreshTokenAdapter createRefreshTokenAdapter,
            IFindRefreshTokenAdapter findRefreshTokenAdapter,
            IUpdateRefreshTokenAdapter updateRefreshTokenAdapter,
            IDeleteRefreshTokenAdapter deleteRefreshTokenAdapter,
            IFindUserUseCase findUserUseCase,
            PasswordEncoder passwordEncoder,
            IJwtService jwtService
    ) {
        this.createRefreshTokenAdapter = createRefreshTokenAdapter;
        this.findRefreshTokenAdapter = findRefreshTokenAdapter;
        this.updateRefreshTokenAdapter = updateRefreshTokenAdapter;
        this.deleteRefreshTokenAdapter = deleteRefreshTokenAdapter;
        this.findUserUseCase = findUserUseCase;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthTokensDtoService login(LoginDtoService dto) {
        log.info("Authenticating user with email: " + dto.email());
        var model = this.findUserUseCase.findByEmail(dto.email());

        if (!model.getAccountValue()) {
            log.warning("Login rejected - account is disabled for email: " + dto.email());
            throw AuthCredentialsException.accountDisabled();
        }
        if (!passwordEncoder.matches(dto.password(), model.getPasswordValue())) {
            log.warning("Login rejected - invalid credentials for email: " + dto.email());
            throw AuthCredentialsException.invalidCredentials();
        }

        var accessToken = this.jwtService.generateAccessToken(
                model.getIdValue(),
                model.getEmailValue(),
                model.getRoleValue().getValue()
        );
        var refreshToken = this.jwtService.generateRefreshToken();
        var expireAt = this.jwtService.calculateRefreshTokenExpiration();

        var refreshTokenModel = RefreshTokenMapperService.toRefreshTokenModel(
                new CreateRefreshTokenDtoService(model.getIdValue(), refreshToken, expireAt)
        );
        this.createRefreshTokenAdapter.create(refreshTokenModel);
        log.info("Login successful and refresh token issued for user: " + model.getIdValue());

        return new AuthTokensDtoService(accessToken, refreshToken, expireAt);
    }

    @Override
    @Transactional
    public AuthTokensDtoService refresh(RefreshTokenRequestDtoService dto) {
        log.info("Processing refresh token rotation");
        var token = findByTokenHash(dto.refreshToken());
        token.validateActive();

        var user = this.findUserUseCase.findById(token.getUserIdValue());
        if (!user.getAccountValue()) {
            log.warning("Refresh rejected - account is disabled for user: " + user.getIdValue());
            throw AuthCredentialsException.accountDisabled();
        }

        token = token.revoke();
        var accessToken = this.jwtService.generateAccessToken(
                user.getIdValue(),
                user.getEmailValue(),
                user.getRoleValue().getValue()
        );
        var refreshToken = this.jwtService.generateRefreshToken();
        var expireAt = this.jwtService.calculateRefreshTokenExpiration();

        var refreshTokenModel = RefreshTokenMapperService.toRefreshTokenModel(
                new CreateRefreshTokenDtoService(user.getIdValue(), refreshToken, expireAt)
        );
        this.updateRefreshTokenAdapter.update(token);
        this.createRefreshTokenAdapter.create(refreshTokenModel);
        log.info("Refresh token rotated successfully for user: " + user.getIdValue());

        return new AuthTokensDtoService(accessToken, refreshToken, expireAt);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        log.info("Processing logout for single session");
        var token = findByTokenHash(refreshToken);
        deleteRefreshTokenAdapter.delete(token.getIdValue());
        log.info("Refresh token deleted with ID: " + token.getIdValue());
    }

    @Override
    @Transactional
    public void logoutAllByUserId(UUID userId) {
        log.info("Deleting all refresh tokens for user: " + userId);
        this.deleteRefreshTokenAdapter.deleteByUserId(userId);
    }

    @Override
    public RefreshTokenModel findByTokenHash(String tokenHash) {
        log.info("Finding refresh token by token hash");
        return findRefreshTokenAdapter.findByTokenHash(tokenHash)
                .orElseThrow(() -> {
                    log.warning("Refresh token not found by hash");
                    return RefreshTokenException.notFound();
                });
    }

    @Override
    public RefreshTokenModel findById(Long id) {
        log.info("Finding refresh token with id: " + id);
        return findRefreshTokenAdapter.findById(id)
                .orElseThrow(() -> {
                    log.warning("Refresh token not found with id: " + id);
                    return RefreshTokenException.notFound();
                });
    }

    @Override
    public List<RefreshTokenModel> findAllFiltered(UUID userId, Boolean isRevoked) {
        log.info(String.format("Finding refresh tokens with filters [userId: %s, isRevoked: %s]", userId, isRevoked));
        return this.findRefreshTokenAdapter.findAllFiltered(userId, isRevoked);
    }

}
