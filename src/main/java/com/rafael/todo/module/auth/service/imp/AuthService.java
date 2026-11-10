package com.rafael.todo.module.auth.service.imp;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rafael.todo.module.auth.exception.AuthCredentialsException;
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
import com.rafael.todo.module.auth.service.dto.LoginDtoService;
import com.rafael.todo.module.auth.service.dto.RefreshTokenRequestDtoService;
import com.rafael.todo.module.user.service.IFindUserUseCase;

import lombok.extern.java.Log;

@Service
@Log
public final class AuthService implements
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

    @Autowired
    public AuthService(
            ICreateRefreshTokenAdapter createRefreshTokenAdapter,
            IFindRefreshTokenAdapter findRefreshTokenAdapter,
            IUpdateRefreshTokenAdapter updateRefreshTokenAdapter,
            IDeleteRefreshTokenAdapter deleteRefreshTokenAdapter,
            IFindUserUseCase findUserUseCase,
            PasswordEncoder passwordEncoder) {
        this.createRefreshTokenAdapter = createRefreshTokenAdapter;
        this.findRefreshTokenAdapter = findRefreshTokenAdapter;
        this.updateRefreshTokenAdapter = updateRefreshTokenAdapter;
        this.deleteRefreshTokenAdapter = deleteRefreshTokenAdapter;
        this.findUserUseCase = findUserUseCase;
        this.passwordEncoder = passwordEncoder;
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

        return null;
    }

    @Override
    public AuthTokensDtoService refresh(RefreshTokenRequestDtoService dto) {
        return null;
    }

    @Override
    public void logout(String refreshToken) {

    }

    @Override
    public void logoutAllByUserId(UUID userId) {

    }

    @Override
    public RefreshTokenModel findById(Long id) {
        return null;
    }

    @Override
    public RefreshTokenModel findByTokenHash(String tokenHash) {
        return null;
    }

    @Override
    public List<RefreshTokenModel> findAllFiltered(UUID userId, Boolean isRevoked) {
        return null;
    }

}
