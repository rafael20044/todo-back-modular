package com.rafael.todo.module.user.model.vo;

import com.rafael.todo.module.user.exception.UserNameException;
import com.rafael.todo.shared.MyConst;

public record UserName(String value) {

    public UserName {
        if (value == null || value.trim().isBlank()) {
            throw UserNameException.required();
        }
        value = value.trim();
        int min = Integer.parseInt(MyConst.MIN_NAME_LENGTH.getValue());
        int max = Integer.parseInt(MyConst.MAX_NAME_LENGTH.getValue());
        if (value.length() < min) {
            throw UserNameException.tooShort();
        }
        if (value.length() > max) {
            throw UserNameException.tooLong();
        }
    }

    public static UserName of(String value) {
        return new UserName(value);
    }

    @Override
    public String toString() {
        return value;
    }

}
