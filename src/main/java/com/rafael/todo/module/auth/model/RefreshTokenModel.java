package com.rafael.todo.module.auth.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.rafael.todo.module.auth.exception.RefreshTokenException;
import com.rafael.todo.module.auth.model.vo.RefreshTokenExpiration;
import com.rafael.todo.module.auth.model.vo.RefreshTokenHash;
import com.rafael.todo.module.auth.model.vo.RefreshTokenId;
import com.rafael.todo.module.auth.model.vo.RefreshTokenRevoked;
import com.rafael.todo.module.auth.model.vo.RefreshTokenUserId;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class RefreshTokenModel {

    private final RefreshTokenId id;
    private final RefreshTokenUserId userId;
    private final RefreshTokenHash tokenHash;
    private final RefreshTokenExpiration expiresAt;
    private final RefreshTokenRevoked revoked;
    private final LocalDateTime createdAt;

    public static RefreshTokenModel create(
            RefreshTokenUserId userId,
            RefreshTokenHash tokenHash,
            RefreshTokenExpiration expiresAt
    ) {
        return new RefreshTokenModel(
                null,
                userId,
                tokenHash,
                expiresAt,
                RefreshTokenRevoked.active(),
                LocalDateTime.now()
        );
    }

    public static RefreshTokenModel reconstitute(
            RefreshTokenId id,
            RefreshTokenUserId userId,
            RefreshTokenHash tokenHash,
            RefreshTokenExpiration expiresAt,
            RefreshTokenRevoked revoked,
            LocalDateTime createdAt
    ) {
        return new RefreshTokenModel(id, userId, tokenHash, expiresAt, revoked, createdAt);
    }

    public RefreshTokenModel revoke() {
        return copy(this.tokenHash, this.expiresAt, this.revoked.revoke());
    }

    public boolean isExpired() {
        return this.expiresAt.isExpired();
    }

    public void validateActive() {
        if (this.revoked.isRevoked()) {
            throw RefreshTokenException.revoked();
        }
        if (isExpired()) {
            throw RefreshTokenException.expired();
        }
    }

    public Long getIdValue() {
        return id != null ? id.value() : null;
    }

    public UUID getUserIdValue() {
        return userId.value();
    }

    public String getTokenHashValue() {
        return tokenHash.value();
    }

    public LocalDateTime getExpiresAtValue() {
        return expiresAt.value();
    }

    public boolean isRevokedValue() {
        return revoked.isRevoked();
    }

    private RefreshTokenModel copy(
            RefreshTokenHash tokenHash,
            RefreshTokenExpiration expiresAt,
            RefreshTokenRevoked revoked
    ) {
        return new RefreshTokenModel(
                this.id,
                this.userId,
                tokenHash,
                expiresAt,
                revoked,
                this.createdAt
        );
    }

}

