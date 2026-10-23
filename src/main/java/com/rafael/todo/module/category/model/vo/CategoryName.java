package com.rafael.todo.module.category.model.vo;

import com.rafael.todo.module.category.exception.CategoryNameException;
import com.rafael.todo.shared.MyConst;

public record CategoryName(String value) {

    public CategoryName {
        if (value == null || value.trim().isBlank()) {
            throw CategoryNameException.required();
        }
        value = value.trim();
        int min = Integer.parseInt(MyConst.MIN_CATEGORY_NAME_LENGTH.getValue());
        int max = Integer.parseInt(MyConst.MAX_CATEGORY_NAME_LENGTH.getValue());
        if (value.length() < min) {
            throw CategoryNameException.tooShort();
        }
        if (value.length() > max) {
            throw CategoryNameException.tooLong();
        }
    }

    public static CategoryName of(String value) {
        return new CategoryName(value);
    }

    @Override
    public String toString() {
        return value;
    }

}
