package com.rafael.todo.module.user.exception;

import com.rafael.todo.shared.DomainException;

public final class UserUuidException extends DomainException {

    private static final String CODE_UUID_NOT_FOUND = "user.uuid.not.found";
    private static final String MSG_UUID_NOT_FOUND = "User with the specified UUID was not found";

    private static final String CODE_UUID_REQUIRED = "user.uuid.required";
    private static final String MSG_UUID_REQUIRED = "User UUID cannot be null or empty";

    private static final String CODE_UUID_INVALID_FORMAT = "user.uuid.invalid.format";
    private static final String MSG_UUID_INVALID_FORMAT = "User UUID format is invalid";

    private UserUuidException(String code, String message) {
        super(code, message);
    }

    public static UserUuidException notFound() {
        return new UserUuidException(CODE_UUID_NOT_FOUND, MSG_UUID_NOT_FOUND);
    }

    public static UserUuidException required() {
        return new UserUuidException(CODE_UUID_REQUIRED, MSG_UUID_REQUIRED);
    }

    public static UserUuidException invalidFormat() {
        return new UserUuidException(CODE_UUID_INVALID_FORMAT, MSG_UUID_INVALID_FORMAT);
    }

}
