package com.rafael.todo.module.category.exception;

import com.rafael.todo.shared.DomainException;

public final class CategoryIdException extends DomainException {

    private static final String CODE_ID_NOT_FOUND = "category.id.not.found";
    private static final String MSG_ID_NOT_FOUND = "Category with the specified ID was not found";

    private static final String CODE_ID_REQUIRED = "category.id.required";
    private static final String MSG_ID_REQUIRED = "Category ID cannot be null";

    private static final String CODE_ID_INVALID = "category.id.invalid";
    private static final String MSG_ID_INVALID = "Category ID must be greater than zero";

    private CategoryIdException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static CategoryIdException notFound() {
        return new CategoryIdException(CODE_ID_NOT_FOUND, MSG_ID_NOT_FOUND, 404);
    }

    public static CategoryIdException required() {
        return new CategoryIdException(CODE_ID_REQUIRED, MSG_ID_REQUIRED, 400);
    }

    public static CategoryIdException invalid() {
        return new CategoryIdException(CODE_ID_INVALID, MSG_ID_INVALID, 400);
    }

}
