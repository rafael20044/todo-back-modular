package com.rafael.todo.module.user.exception;

import com.rafael.todo.shared.DomainException;

public final class UserRoleException extends DomainException {

    private static final String CODE_ROLE_REQUIRED = "user.role.required";
    private static final String MSG_ROLE_REQUIRED = "User role cannot be null or empty";

    private static final String CODE_ROLE_INVALID = "user.role.invalid";
    private static final String MSG_ROLE_INVALID = "User role is invalid or unsupported";

    private UserRoleException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static UserRoleException required() {
        return new UserRoleException(CODE_ROLE_REQUIRED, MSG_ROLE_REQUIRED, 400);
    }

    public static UserRoleException invalid() {
        return new UserRoleException(CODE_ROLE_INVALID, MSG_ROLE_INVALID, 400);
    }

}
