package com.rafael.todo.shared;

import lombok.Getter;

@Getter 
public enum MyConst {

    MAX_EMAIL_LENGTH("255"),
    MAX_NAME_LENGTH("100"),
    MIN_NAME_LENGTH("2"),
    MIN_PASSWORD_LENGTH("8"),
    EMAIL_REGEX("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"),
    MAX_CATEGORY_NAME_LENGTH("50"),
    MIN_CATEGORY_NAME_LENGTH("2"),
    HEX_COLOR_REGEX("^#[A-Fa-f0-9]{6}$"),
    MAX_TASK_TITLE_LENGTH("150"),
    MIN_TASK_TITLE_LENGTH("2"),
    MAX_TASK_DESCRIPTION_LENGTH("2000");

    private final String value;

    MyConst(String value) {
        this.value = value;
    }

}
