package com.rafael.todo.module.user.model.vo;

import java.util.UUID;

import com.rafael.todo.module.user.exception.UserUuidException;

public record UserId(UUID value) {

    public UserId {
        if (value == null) {
            throw UserUuidException.required();
        }
    }

    public static UserId random() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }

    public static UserId of(String value) {
        if (value == null || value.trim().isBlank()) {
            throw UserUuidException.required();
        }
        try {
            return new UserId(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException e) {
            throw UserUuidException.invalidFormat();
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
