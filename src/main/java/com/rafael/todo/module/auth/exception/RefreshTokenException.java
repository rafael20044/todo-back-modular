package com.rafael.todo.module.auth.exception;

import com.rafael.todo.shared.DomainException;
import com.rafael.todo.shared.MyConst;

public final class RefreshTokenException extends DomainException {

    private static final String CODE_TOKEN_REQUIRED = "auth.refresh.token.required";
    private static final String MSG_TOKEN_REQUIRED = "Refresh token cannot be null or empty";

    private static final String CODE_TOKEN_TOO_LONG = "auth.refresh.token.too.long";
    private static final String MSG_TOKEN_TOO_LONG = "Refresh token exceeds the maximum allowed length (%s characters)";

    private static final String CODE_TOKEN_NOT_FOUND = "auth.refresh.token.not.found";
    private static final String MSG_TOKEN_NOT_FOUND = "Refresh token was not found";

    private static final String CODE_TOKEN_EXPIRED = "auth.refresh.token.expired";
    private static final String MSG_TOKEN_EXPIRED = "Refresh token has expired";

    private static final String CODE_TOKEN_REVOKED = "auth.refresh.token.revoked";
    private static final String MSG_TOKEN_REVOKED = "Refresh token has been revoked";

    private static final String CODE_TOKEN_ALREADY_REVOKED = "auth.refresh.token.already.revoked";
    private static final String MSG_TOKEN_ALREADY_REVOKED = "Refresh token is already revoked";

    private RefreshTokenException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static RefreshTokenException required() {
        return new RefreshTokenException(CODE_TOKEN_REQUIRED, MSG_TOKEN_REQUIRED, 400);
    }

    public static RefreshTokenException tooLong() {
        return new RefreshTokenException(
            CODE_TOKEN_TOO_LONG,
            String.format(MSG_TOKEN_TOO_LONG, MyConst.MAX_TOKEN_HASH_LENGTH.getValue()),
            400
        );
    }

    public static RefreshTokenException notFound() {
        return new RefreshTokenException(CODE_TOKEN_NOT_FOUND, MSG_TOKEN_NOT_FOUND, 404);
    }

    public static RefreshTokenException expired() {
        return new RefreshTokenException(CODE_TOKEN_EXPIRED, MSG_TOKEN_EXPIRED, 401);
    }

    public static RefreshTokenException revoked() {
        return new RefreshTokenException(CODE_TOKEN_REVOKED, MSG_TOKEN_REVOKED, 401);
    }

    public static RefreshTokenException alreadyRevoked() {
        return new RefreshTokenException(CODE_TOKEN_ALREADY_REVOKED, MSG_TOKEN_ALREADY_REVOKED, 400);
    }

}

