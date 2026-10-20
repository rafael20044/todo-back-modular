package com.rafael.todo.module.user.exception;

import com.rafael.todo.shared.DomainException;
import com.rafael.todo.shared.MyConst;

public final class UserPasswordException extends DomainException {

    private static final String CODE_PASSWORD_REQUIRED = "user.password.required";
    private static final String MSG_PASSWORD_REQUIRED = "User password cannot be null or empty";

    private static final String CODE_PASSWORD_TOO_SHORT = "user.password.too.short";
    private static final String MSG_PASSWORD_TOO_SHORT = "User password must be at least %s characters long";

    private static final String CODE_PASSWORD_INVALID_HASH = "user.password.invalid.hash";
    private static final String MSG_PASSWORD_INVALID_HASH = "User password hash is invalid or malformed";

    private UserPasswordException(String code, String message) {
        super(code, message);
    }

    public static UserPasswordException required() {
        return new UserPasswordException(CODE_PASSWORD_REQUIRED, MSG_PASSWORD_REQUIRED);
    }

    public static UserPasswordException tooShort() {
        return new UserPasswordException(CODE_PASSWORD_TOO_SHORT,
                String.format(MSG_PASSWORD_TOO_SHORT, MyConst.MIN_PASSWORD_LENGTH.getValue()));
    }

    public static UserPasswordException invalidHash() {
        return new UserPasswordException(CODE_PASSWORD_INVALID_HASH, MSG_PASSWORD_INVALID_HASH);
    }

}
