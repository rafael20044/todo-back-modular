package com.rafael.todo.module.auth.model.vo;

import com.rafael.todo.module.auth.exception.RefreshTokenIdException;

public record RefreshTokenId(Long value) {

    public RefreshTokenId {
        if (value == null) {
            throw RefreshTokenIdException.required();
        }
        if (value <= 0) {
            throw RefreshTokenIdException.invalid();
        }
    }

    public static RefreshTokenId of(Long value) {
        return new RefreshTokenId(value);
    }

    public static RefreshTokenId of(String value) {
        if (value == null || value.trim().isBlank()) {
            throw RefreshTokenIdException.required();
        }
        try {
            return new RefreshTokenId(Long.parseLong(value.trim()));
        } catch (NumberFormatException e) {
            throw RefreshTokenIdException.invalid();
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }

}

