package com.rafael.todo.module.auth.model.vo;

import com.rafael.todo.module.auth.exception.RefreshTokenException;
import com.rafael.todo.shared.MyConst;

public record RefreshTokenHash(String value) {

    public RefreshTokenHash {
        if (value == null || value.trim().isBlank()) {
            throw RefreshTokenException.required();
        }
        value = value.trim();
        int max = Integer.parseInt(MyConst.MAX_TOKEN_HASH_LENGTH.getValue());
        if (value.length() > max) {
            throw RefreshTokenException.tooLong();
        }
    }

    public static RefreshTokenHash of(String value) {
        return new RefreshTokenHash(value);
    }

    @Override
    public String toString() {
        return value;
    }

}

