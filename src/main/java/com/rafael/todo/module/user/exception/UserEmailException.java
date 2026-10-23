package com.rafael.todo.module.user.exception;

import com.rafael.todo.shared.DomainException;
import com.rafael.todo.shared.MyConst;

public final class UserEmailException extends DomainException {

    private static final String CODE_EMAIL_REQUIRED = "user.email.required";
    private static final String MSG_EMAIL_REQUIRED = "User email cannot be null or empty";

    private static final String CODE_EMAIL_INVALID_FORMAT = "user.email.invalid.format";
    private static final String MSG_EMAIL_INVALID_FORMAT = "User email format is invalid";

    private static final String CODE_EMAIL_TOO_LONG = "user.email.too.long";
    private static final String MSG_EMAIL_TOO_LONG = "User email exceeds the maximum allowed length (%s characters)";

    private static final String CODE_EMAIL_ALREADY_EXISTS = "user.email.already.exists";
    private static final String MSG_EMAIL_ALREADY_EXISTS = "User with this email already exists";

    private UserEmailException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static UserEmailException required() {
        return new UserEmailException(CODE_EMAIL_REQUIRED, MSG_EMAIL_REQUIRED, 400);
    }

    public static UserEmailException invalidFormat() {
        return new UserEmailException(CODE_EMAIL_INVALID_FORMAT, MSG_EMAIL_INVALID_FORMAT, 400);
    }

    public static UserEmailException tooLong() {
        return new UserEmailException(CODE_EMAIL_TOO_LONG,
                String.format(MSG_EMAIL_TOO_LONG, MyConst.MAX_EMAIL_LENGTH.getValue()), 400);
    }

    public static UserEmailException alreadyExists() {
        return new UserEmailException(CODE_EMAIL_ALREADY_EXISTS, MSG_EMAIL_ALREADY_EXISTS, 409);
    }

}
