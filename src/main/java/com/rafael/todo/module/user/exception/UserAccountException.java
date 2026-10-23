package com.rafael.todo.module.user.exception;

import com.rafael.todo.shared.DomainException;

public final class UserAccountException extends DomainException {

    private static final String CODE_ACCOUNT_DISABLED = "user.account.disabled";
    private static final String MSG_ACCOUNT_DISABLED = "User account is inactive or disabled";

    private static final String CODE_ACCOUNT_ALREADY_ACTIVE = "user.account.already.active";
    private static final String MSG_ACCOUNT_ALREADY_ACTIVE = "User account is already active";

    private static final String CODE_ACCOUNT_ALREADY_INACTIVE = "user.account.already.inactive";
    private static final String MSG_ACCOUNT_ALREADY_INACTIVE = "User account is already inactive";

    private UserAccountException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static UserAccountException disabled() {
        return new UserAccountException(CODE_ACCOUNT_DISABLED, MSG_ACCOUNT_DISABLED, 400);
    }

    public static UserAccountException alreadyActive() {
        return new UserAccountException(CODE_ACCOUNT_ALREADY_ACTIVE, MSG_ACCOUNT_ALREADY_ACTIVE, 400);
    }

    public static UserAccountException alreadyInactive() {
        return new UserAccountException(CODE_ACCOUNT_ALREADY_INACTIVE, MSG_ACCOUNT_ALREADY_INACTIVE, 400);
    }

}
