package com.rafael.todo.module.auth.model.vo;

import java.util.UUID;

import com.rafael.todo.module.auth.exception.RefreshTokenUserIdException;

public record RefreshTokenUserId(UUID value) {

    public RefreshTokenUserId {
        if (value == null) {
            throw RefreshTokenUserIdException.required();
        }
    }

    public static RefreshTokenUserId of(UUID value) {
        return new RefreshTokenUserId(value);
    }

    public static RefreshTokenUserId of(String value) {
        if (value == null || value.trim().isBlank()) {
            throw RefreshTokenUserIdException.required();
        }
        try {
            return new RefreshTokenUserId(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException e) {
            throw RefreshTokenUserIdException.invalidFormat();
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }

}

