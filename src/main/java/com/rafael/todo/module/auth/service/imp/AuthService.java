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

import java.time.LocalDateTime;
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
    public AuthTokensDtoService login(LoginDtoService dto) {
        var model = this.findUserUseCase.findByEmail(dto.email());

        if (!model.getAccountValue()) {
            throw AuthCredentialsException.accountDisabled();
        }
        if (!passwordEncoder.matches(dto.password(), model.getPasswordValue())) {
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

        return new AuthTokensDtoService(accessToken, refreshToken, expireAt);
    }

    @Override
    public AuthTokensDtoService refresh(RefreshTokenRequestDtoService dto) {
        var token = findByTokenHash(dto.refreshToken());
        if (token.getRevoked().isRevoked()) {
            throw RefreshTokenException.revoked();
        }
        if (token.getExpiresAtValue().isBefore(LocalDateTime.now())) {
            throw RefreshTokenException.expired();
        }

        token = token.revoke();
        var user = this.findUserUseCase.findById(token.getUserIdValue());
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
        
        return new AuthTokensDtoService(accessToken, refreshToken, expireAt);
    }

    @Override
    public void logoutAllByUserId(UUID userId) {
        
    }


    @Override
    public RefreshTokenModel findByTokenHash(String tokenHash) {
        return findRefreshTokenAdapter.findByTokenHash(tokenHash).orElseThrow(() -> RefreshTokenException.notFound());
    }

    @Override
    public RefreshTokenModel findById(Long id) {
        return findRefreshTokenAdapter.findById(id).orElseThrow(() -> RefreshTokenException.notFound());
    }

    @Override
    public List<RefreshTokenModel> findAllFiltered(UUID userId, Boolean isRevoked) {
       return this.findRefreshTokenAdapter.findAllFiltered(userId, isRevoked);
    }

    @Override
    public void logout(String refreshToken) {
    }

}
