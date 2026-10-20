package com.rafael.todo.shared;

import lombok.Getter;

@Getter 
public enum MyConst {

    MAX_EMAIL_LENGTH("255"),
    MAX_NAME_LENGTH("100"),
    MIN_NAME_LENGTH("2"),
    MIN_PASSWORD_LENGTH("8"),
    EMAIL_REGEX("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    private final String value;

    MyConst(String value) {
        this.value = value;
    }


}
