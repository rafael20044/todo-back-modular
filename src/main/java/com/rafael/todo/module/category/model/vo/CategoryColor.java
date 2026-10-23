package com.rafael.todo.module.category.model.vo;

import java.util.regex.Pattern;

import com.rafael.todo.module.category.exception.CategoryColorException;
import com.rafael.todo.shared.MyConst;

public record CategoryColor(String value) {

    private static final Pattern PATTERN = Pattern.compile(MyConst.HEX_COLOR_REGEX.getValue());
    private static final String DEFAULT_COLOR = "#3B82F6";

    public CategoryColor {
        if (value == null || value.trim().isBlank()) {
            throw CategoryColorException.required();
        }
        value = value.trim().toUpperCase();
        if (!PATTERN.matcher(value).matches()) {
            throw CategoryColorException.invalidFormat();
        }
    }

    public static CategoryColor of(String value) {
        return new CategoryColor(value);
    }

    public static CategoryColor defaultColor() {
        return new CategoryColor(DEFAULT_COLOR);
    }

    @Override
    public String toString() {
        return value;
    }

}
