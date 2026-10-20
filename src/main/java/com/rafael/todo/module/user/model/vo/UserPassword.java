package com.rafael.todo.module.user.model.vo;

import com.rafael.todo.module.user.exception.UserPasswordException;
import com.rafael.todo.shared.MyConst;

public record UserPassword(String value) {

    public UserPassword {
        if (value == null || value.trim().isBlank()) {
            throw UserPasswordException.required();
        }
    }

    public static UserPassword fromRaw(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isBlank()) {
            throw UserPasswordException.required();
        }
        int min = Integer.parseInt(MyConst.MIN_PASSWORD_LENGTH.getValue());
        if (rawPassword.length() < min) {
            throw UserPasswordException.tooShort();
        }
        return new UserPassword(rawPassword);
    }

    public static UserPassword fromHash(String hash) {
        if (hash == null || hash.trim().isBlank()) {
            throw UserPasswordException.invalidHash();
        }
        return new UserPassword(hash);
    }

    @Override
    public String toString() {
        return "******";
    }

}
