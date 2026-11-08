package com.rafael.todo.module.auth.exception;

import com.rafael.todo.shared.DomainException;

public final class RefreshTokenExpirationException extends DomainException {

    private static final String CODE_EXPIRATION_REQUIRED = "auth.expiration.required";
    private static final String MSG_EXPIRATION_REQUIRED = "Refresh token expiration date cannot be null";

    private static final String CODE_EXPIRATION_INVALID = "auth.expiration.invalid";
    private static final String MSG_EXPIRATION_INVALID = "Refresh token expiration date format or value is invalid";

    private RefreshTokenExpirationException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static RefreshTokenExpirationException required() {
        return new RefreshTokenExpirationException(CODE_EXPIRATION_REQUIRED, MSG_EXPIRATION_REQUIRED, 400);
    }

    public static RefreshTokenExpirationException invalid() {
        return new RefreshTokenExpirationException(CODE_EXPIRATION_INVALID, MSG_EXPIRATION_INVALID, 400);
    }

}

