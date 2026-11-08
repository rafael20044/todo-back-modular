package com.rafael.todo.module.auth.exception;

import com.rafael.todo.shared.DomainException;

public final class AuthCredentialsException extends DomainException {

    private static final String CODE_INVALID_CREDENTIALS = "auth.credentials.invalid";
    private static final String MSG_INVALID_CREDENTIALS = "Invalid email or password";

    private static final String CODE_ACCOUNT_DISABLED = "auth.account.disabled";
    private static final String MSG_ACCOUNT_DISABLED = "User account is disabled or inactive";

    private static final String CODE_INVALID_ACCESS_TOKEN = "auth.access.token.invalid";
    private static final String MSG_INVALID_ACCESS_TOKEN = "Access token is invalid or malformed";

    private static final String CODE_EXPIRED_ACCESS_TOKEN = "auth.access.token.expired";
    private static final String MSG_EXPIRED_ACCESS_TOKEN = "Access token has expired";

    private AuthCredentialsException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static AuthCredentialsException invalidCredentials() {
        return new AuthCredentialsException(CODE_INVALID_CREDENTIALS, MSG_INVALID_CREDENTIALS, 401);
    }

    public static AuthCredentialsException accountDisabled() {
        return new AuthCredentialsException(CODE_ACCOUNT_DISABLED, MSG_ACCOUNT_DISABLED, 403);
    }

    public static AuthCredentialsException invalidAccessToken() {
        return new AuthCredentialsException(CODE_INVALID_ACCESS_TOKEN, MSG_INVALID_ACCESS_TOKEN, 401);
    }

    public static AuthCredentialsException expiredAccessToken() {
        return new AuthCredentialsException(CODE_EXPIRED_ACCESS_TOKEN, MSG_EXPIRED_ACCESS_TOKEN, 401);
    }

}

