package com.rafael.todo.module.auth.exception;

import com.rafael.todo.shared.DomainException;

public final class RefreshTokenIdException extends DomainException {

    private static final String CODE_ID_NOT_FOUND = "auth.refresh.token.id.not.found";
    private static final String MSG_ID_NOT_FOUND = "Refresh token with the specified ID was not found";

    private static final String CODE_ID_REQUIRED = "auth.refresh.token.id.required";
    private static final String MSG_ID_REQUIRED = "Refresh token ID cannot be null";

    private static final String CODE_ID_INVALID = "auth.refresh.token.id.invalid";
    private static final String MSG_ID_INVALID = "Refresh token ID must be greater than zero";

    private RefreshTokenIdException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static RefreshTokenIdException notFound() {
        return new RefreshTokenIdException(CODE_ID_NOT_FOUND, MSG_ID_NOT_FOUND, 404);
    }

    public static RefreshTokenIdException required() {
        return new RefreshTokenIdException(CODE_ID_REQUIRED, MSG_ID_REQUIRED, 400);
    }

    public static RefreshTokenIdException invalid() {
        return new RefreshTokenIdException(CODE_ID_INVALID, MSG_ID_INVALID, 400);
    }

}

