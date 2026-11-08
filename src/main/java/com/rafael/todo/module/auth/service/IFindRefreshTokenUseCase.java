package com.rafael.todo.module.auth.service;

import java.util.List;
import java.util.UUID;

import com.rafael.todo.module.auth.model.RefreshTokenModel;

public interface IFindRefreshTokenUseCase {

    RefreshTokenModel findById(Long id);

    RefreshTokenModel findByTokenHash(String tokenHash);

    List<RefreshTokenModel> findAllFiltered(UUID userId, Boolean isRevoked);

}

