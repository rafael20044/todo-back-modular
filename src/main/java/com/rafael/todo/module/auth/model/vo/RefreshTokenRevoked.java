package com.rafael.todo.module.auth.model.vo;

import com.rafael.todo.module.auth.exception.RefreshTokenException;

public record RefreshTokenRevoked(boolean isRevoked) {

    public static RefreshTokenRevoked active() {
        return new RefreshTokenRevoked(false);
    }

    public static RefreshTokenRevoked revoked() {
        return new RefreshTokenRevoked(true);
    }

    public static RefreshTokenRevoked of(boolean isRevoked) {
        return new RefreshTokenRevoked(isRevoked);
    }

    public RefreshTokenRevoked revoke() {
        if (this.isRevoked) {
            throw RefreshTokenException.alreadyRevoked();
        }
        return new RefreshTokenRevoked(true);
    }

    @Override
    public String toString() {
        return String.valueOf(isRevoked);
    }

}

