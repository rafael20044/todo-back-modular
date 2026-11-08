package com.rafael.todo.module.auth.model.vo;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.rafael.todo.module.auth.exception.RefreshTokenExpirationException;

public record RefreshTokenExpiration(LocalDateTime value) {

    public RefreshTokenExpiration {
        if (value == null) {
            throw RefreshTokenExpirationException.required();
        }
    }

    public static RefreshTokenExpiration of(LocalDateTime value) {
        return new RefreshTokenExpiration(value);
    }

    public static RefreshTokenExpiration of(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.trim().isBlank()) {
            throw RefreshTokenExpirationException.required();
        }
        try {
            return new RefreshTokenExpiration(LocalDateTime.parse(isoDateTime.trim()));
        } catch (DateTimeParseException e) {
            throw RefreshTokenExpirationException.invalid();
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }

}

