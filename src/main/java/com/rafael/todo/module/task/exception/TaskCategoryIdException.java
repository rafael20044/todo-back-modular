package com.rafael.todo.module.task.exception;

import com.rafael.todo.shared.DomainException;

public final class TaskCategoryIdException extends DomainException {

    private static final String CODE_CATEGORY_ID_INVALID = "task.category.id.invalid";
    private static final String MSG_CATEGORY_ID_INVALID = "Category ID for the task must be greater than zero";

    private TaskCategoryIdException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static TaskCategoryIdException invalid() {
        return new TaskCategoryIdException(CODE_CATEGORY_ID_INVALID, MSG_CATEGORY_ID_INVALID, 400);
    }

}
