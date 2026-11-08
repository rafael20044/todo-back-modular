package com.rafael.todo.module.auth.repository.mapper;

import com.rafael.todo.module.auth.model.RefreshTokenModel;
import com.rafael.todo.module.auth.model.vo.RefreshTokenExpiration;
import com.rafael.todo.module.auth.model.vo.RefreshTokenHash;
import com.rafael.todo.module.auth.model.vo.RefreshTokenId;
import com.rafael.todo.module.auth.model.vo.RefreshTokenRevoked;
import com.rafael.todo.module.auth.model.vo.RefreshTokenUserId;
import com.rafael.todo.module.auth.repository.entity.RefreshTokenEntity;

public final class RefreshTokenMapperRepository {

    private RefreshTokenMapperRepository() {}

    public static RefreshTokenEntity toEntity(RefreshTokenModel model) {
        return RefreshTokenEntity.builder()
            .id(model.getIdValue())
            .userId(model.getUserIdValue())
            .tokenHash(model.getTokenHashValue())
            .expiresAt(model.getExpiresAtValue())
            .isRevoked(model.isRevokedValue())
            .createdAt(model.getCreatedAt())
            .build();
    }

    public static RefreshTokenModel toModel(RefreshTokenEntity entity) {
        return RefreshTokenModel.reconstitute(
            RefreshTokenId.of(entity.getId()),
            RefreshTokenUserId.of(entity.getUserId()),
            RefreshTokenHash.of(entity.getTokenHash()),
            RefreshTokenExpiration.of(entity.getExpiresAt()),
            RefreshTokenRevoked.of(entity.isRevoked()),
            entity.getCreatedAt()
        );
    }

}

