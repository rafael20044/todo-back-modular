package com.rafael.todo.module.category.exception;

import com.rafael.todo.shared.DomainException;
import com.rafael.todo.shared.MyConst;

public final class CategoryNameException extends DomainException {

    private static final String CODE_NAME_REQUIRED = "category.name.required";
    private static final String MSG_NAME_REQUIRED = "Category name cannot be null or empty";

    private static final String CODE_NAME_TOO_SHORT = "category.name.too.short";
    private static final String MSG_NAME_TOO_SHORT = "Category name must be at least %s characters long";

    private static final String CODE_NAME_TOO_LONG = "category.name.too.long";
    private static final String MSG_NAME_TOO_LONG = "Category name exceeds the maximum allowed length (%s characters)";

    private static final String CODE_NAME_ALREADY_EXISTS = "category.name.already.exists";
    private static final String MSG_NAME_ALREADY_EXISTS = "A category with this name already exists for this user";

    private CategoryNameException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static CategoryNameException required() {
        return new CategoryNameException(CODE_NAME_REQUIRED, MSG_NAME_REQUIRED, 400);
    }

    public static CategoryNameException tooShort() {
        return new CategoryNameException(CODE_NAME_TOO_SHORT,
                String.format(MSG_NAME_TOO_SHORT, MyConst.MIN_CATEGORY_NAME_LENGTH.getValue()), 400);
    }

    public static CategoryNameException tooLong() {
        return new CategoryNameException(CODE_NAME_TOO_LONG,
                String.format(MSG_NAME_TOO_LONG, MyConst.MAX_CATEGORY_NAME_LENGTH.getValue()), 400);
    }

    public static CategoryNameException alreadyExists() {
        return new CategoryNameException(CODE_NAME_ALREADY_EXISTS, MSG_NAME_ALREADY_EXISTS, 409);
    }

}
