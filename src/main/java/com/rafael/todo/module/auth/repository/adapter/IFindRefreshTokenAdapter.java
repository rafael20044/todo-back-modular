package com.rafael.todo.module.auth.repository.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.rafael.todo.module.auth.model.RefreshTokenModel;

public interface IFindRefreshTokenAdapter {

    Optional<RefreshTokenModel> findById(Long id);

    Optional<RefreshTokenModel> findByTokenHash(String tokenHash);

    List<RefreshTokenModel> findAllByUserId(UUID userId);

    List<RefreshTokenModel> findAllFiltered(UUID userId, Boolean isRevoked);

}

