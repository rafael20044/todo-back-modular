package com.rafael.todo.module.category.exception;

import com.rafael.todo.shared.DomainException;

public final class CategoryUserIdException extends DomainException {

    private static final String CODE_USER_ID_REQUIRED = "category.user.id.required";
    private static final String MSG_USER_ID_REQUIRED = "User ID for the category cannot be null";

    private static final String CODE_USER_ID_INVALID_FORMAT = "category.user.id.invalid.format";
    private static final String MSG_USER_ID_INVALID_FORMAT = "User ID format for the category is invalid";

    private CategoryUserIdException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static CategoryUserIdException required() {
        return new CategoryUserIdException(CODE_USER_ID_REQUIRED, MSG_USER_ID_REQUIRED, 400);
    }

    public static CategoryUserIdException invalidFormat() {
        return new CategoryUserIdException(CODE_USER_ID_INVALID_FORMAT, MSG_USER_ID_INVALID_FORMAT, 400);
    }

}
