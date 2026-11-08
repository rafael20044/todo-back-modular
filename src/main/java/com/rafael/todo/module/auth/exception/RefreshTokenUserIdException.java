package com.rafael.todo.module.auth.exception;

import com.rafael.todo.shared.DomainException;

public final class RefreshTokenUserIdException extends DomainException {

    private static final String CODE_USER_ID_REQUIRED = "auth.user.id.required";
    private static final String MSG_USER_ID_REQUIRED = "User ID for the refresh token cannot be null";

    private static final String CODE_USER_ID_INVALID_FORMAT = "auth.user.id.invalid.format";
    private static final String MSG_USER_ID_INVALID_FORMAT = "User ID format for the refresh token is invalid";

    private RefreshTokenUserIdException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static RefreshTokenUserIdException required() {
        return new RefreshTokenUserIdException(CODE_USER_ID_REQUIRED, MSG_USER_ID_REQUIRED, 400);
    }

    public static RefreshTokenUserIdException invalidFormat() {
        return new RefreshTokenUserIdException(CODE_USER_ID_INVALID_FORMAT, MSG_USER_ID_INVALID_FORMAT, 400);
    }

}

