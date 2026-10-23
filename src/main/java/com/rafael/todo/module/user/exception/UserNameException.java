package com.rafael.todo.module.user.exception;

import com.rafael.todo.shared.DomainException;
import com.rafael.todo.shared.MyConst;

public final class UserNameException extends DomainException {

    private static final String CODE_NAME_REQUIRED = "user.name.required";
    private static final String MSG_NAME_REQUIRED = "User name cannot be null or empty";

    private static final String CODE_NAME_TOO_SHORT = "user.name.too.short";
    private static final String MSG_NAME_TOO_SHORT = "User name must be at least %s characters long";

    private static final String CODE_NAME_TOO_LONG = "user.name.too.long";
    private static final String MSG_NAME_TOO_LONG = "User name exceeds the maximum allowed length (%s characters)";

    private UserNameException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static UserNameException required() {
        return new UserNameException(CODE_NAME_REQUIRED, MSG_NAME_REQUIRED, 400);
    }

    public static UserNameException tooShort() {
        return new UserNameException(CODE_NAME_TOO_SHORT,
                String.format(MSG_NAME_TOO_SHORT, MyConst.MIN_NAME_LENGTH.getValue()), 400);
    }

    public static UserNameException tooLong() {
        return new UserNameException(CODE_NAME_TOO_LONG,
                String.format(MSG_NAME_TOO_LONG, MyConst.MAX_NAME_LENGTH.getValue()), 400);
    }

}
