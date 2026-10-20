package com.rafael.todo.module.user.exception;

import com.rafael.todo.shared.DomainException;

public final class UserAccountException extends DomainException {

    private static final String CODE_ACCOUNT_DISABLED = "user.account.disabled";
    private static final String MSG_ACCOUNT_DISABLED = "User account is inactive or disabled";

    private static final String CODE_ACCOUNT_ALREADY_ACTIVE = "user.account.already.active";
    private static final String MSG_ACCOUNT_ALREADY_ACTIVE = "User account is already active";

    private static final String CODE_ACCOUNT_ALREADY_INACTIVE = "user.account.already.inactive";
    private static final String MSG_ACCOUNT_ALREADY_INACTIVE = "User account is already inactive";

    private UserAccountException(String code, String message) {
        super(code, message);
    }

    public static UserAccountException disabled() {
        return new UserAccountException(CODE_ACCOUNT_DISABLED, MSG_ACCOUNT_DISABLED);
    }

    public static UserAccountException alreadyActive() {
        return new UserAccountException(CODE_ACCOUNT_ALREADY_ACTIVE, MSG_ACCOUNT_ALREADY_ACTIVE);
    }

    public static UserAccountException alreadyInactive() {
        return new UserAccountException(CODE_ACCOUNT_ALREADY_INACTIVE, MSG_ACCOUNT_ALREADY_INACTIVE);
    }

}
