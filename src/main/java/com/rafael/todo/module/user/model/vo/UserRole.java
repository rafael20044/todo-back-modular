package com.rafael.todo.module.user.model.vo;

import com.rafael.todo.module.user.exception.UserRoleException;
import com.rafael.todo.module.user.model.enums.UserRoleEnum;

public record UserRole(UserRoleEnum value) {

    public UserRole {
        if (value == null) {
            throw UserRoleException.required();
        }
    }

    public static UserRole user() {
        return new UserRole(UserRoleEnum.ROLE_USER);
    }

    public static UserRole admin() {
        return new UserRole(UserRoleEnum.ROLE_ADMIN);
    }

    public static UserRole of(String roleName) {
        if (roleName == null || roleName.trim().isBlank()) {
            throw UserRoleException.required();
        }
        try {
            return new UserRole(UserRoleEnum.valueOf(roleName.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw UserRoleException.invalid();
        }
    }

    @Override
    public String toString() {
        return value.getValue();
    }

}
