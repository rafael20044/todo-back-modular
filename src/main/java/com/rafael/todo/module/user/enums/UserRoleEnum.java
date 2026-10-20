package com.rafael.todo.module.user.enums;

import lombok.Getter;

@Getter 
public enum UserRoleEnum {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String value;

    UserRoleEnum(String value) {
        this.value = value;
    }
}
