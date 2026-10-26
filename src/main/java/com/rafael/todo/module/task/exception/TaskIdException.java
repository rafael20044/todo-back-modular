package com.rafael.todo.module.task.exception;

import com.rafael.todo.shared.DomainException;

public final class TaskIdException extends DomainException {

    private static final String CODE_ID_NOT_FOUND = "task.id.not.found";
    private static final String MSG_ID_NOT_FOUND = "Task with the specified ID was not found";

    private static final String CODE_ID_REQUIRED = "task.id.required";
    private static final String MSG_ID_REQUIRED = "Task ID cannot be null";

    private static final String CODE_ID_INVALID = "task.id.invalid";
    private static final String MSG_ID_INVALID = "Task ID must be greater than zero";

    private TaskIdException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static TaskIdException notFound() {
        return new TaskIdException(CODE_ID_NOT_FOUND, MSG_ID_NOT_FOUND, 404);
    }

    public static TaskIdException required() {
        return new TaskIdException(CODE_ID_REQUIRED, MSG_ID_REQUIRED, 400);
    }

    public static TaskIdException invalid() {
        return new TaskIdException(CODE_ID_INVALID, MSG_ID_INVALID, 400);
    }

}
