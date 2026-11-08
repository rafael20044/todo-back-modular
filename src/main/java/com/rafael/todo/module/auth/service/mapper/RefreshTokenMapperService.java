package com.rafael.todo.module.auth.service.mapper;

import com.rafael.todo.module.auth.model.RefreshTokenModel;
import com.rafael.todo.module.auth.model.vo.RefreshTokenExpiration;
import com.rafael.todo.module.auth.model.vo.RefreshTokenHash;
import com.rafael.todo.module.auth.model.vo.RefreshTokenUserId;
import com.rafael.todo.module.auth.service.dto.CreateRefreshTokenDtoService;

public final class RefreshTokenMapperService {

    private RefreshTokenMapperService() {}

    public static RefreshTokenModel toRefreshTokenModel(CreateRefreshTokenDtoService dto) {
        return RefreshTokenModel.create(
            RefreshTokenUserId.of(dto.userId()),
            RefreshTokenHash.of(dto.tokenHash()),
            RefreshTokenExpiration.of(dto.expiresAt())
        );
    }

}

