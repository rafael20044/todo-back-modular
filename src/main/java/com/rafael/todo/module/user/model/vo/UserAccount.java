package com.rafael.todo.module.user.model.vo;

import com.rafael.todo.module.user.exception.UserAccountException;

public record UserAccount(boolean isActive) {

    public static UserAccount active() {
        return new UserAccount(true);
    }

    public static UserAccount inactive() {
        return new UserAccount(false);
    }

    public static UserAccount of(boolean isActive) {
        return new UserAccount(isActive);
    }

    public UserAccount activate() {
        if (this.isActive) {
            throw UserAccountException.alreadyActive();
        }
        return new UserAccount(true);
    }

    public UserAccount deactivate() {
        if (!this.isActive) {
            throw UserAccountException.alreadyInactive();
        }
        return new UserAccount(false);
    }

}
