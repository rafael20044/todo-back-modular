package com.rafael.todo.module.category.exception;

import com.rafael.todo.shared.DomainException;

public final class CategoryColorException extends DomainException {

    private static final String CODE_COLOR_REQUIRED = "category.color.required";
    private static final String MSG_COLOR_REQUIRED = "Category color cannot be null or empty";

    private static final String CODE_COLOR_INVALID_FORMAT = "category.color.invalid.format";
    private static final String MSG_COLOR_INVALID_FORMAT = "Category color must be a valid 6-digit hex code (e.g. #3B82F6)";

    private CategoryColorException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static CategoryColorException required() {
        return new CategoryColorException(CODE_COLOR_REQUIRED, MSG_COLOR_REQUIRED, 400);
    }

    public static CategoryColorException invalidFormat() {
        return new CategoryColorException(CODE_COLOR_INVALID_FORMAT, MSG_COLOR_INVALID_FORMAT, 400);
    }

}
