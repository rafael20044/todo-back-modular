package com.rafael.todo.module.user.model.vo;

import java.util.regex.Pattern;

import com.rafael.todo.module.user.exception.UserEmailException;
import com.rafael.todo.shared.MyConst;

public record UserEmail(String value) {

    private static final Pattern PATTERN = Pattern.compile(MyConst.EMAIL_REGEX.getValue());

    public UserEmail {
        if (value == null || value.trim().isBlank()) {
            throw UserEmailException.required();
        }
        value = value.trim().toLowerCase();
        if (value.length() > Integer.parseInt(MyConst.MAX_EMAIL_LENGTH.getValue())) {
            throw UserEmailException.tooLong();
        }
        if (!PATTERN.matcher(value).matches()) {
            throw UserEmailException.invalidFormat();
        }
    }

    public static UserEmail of(String value) {
        return new UserEmail(value);
    }

    @Override
    public String toString() {
        return value;
    }

}
