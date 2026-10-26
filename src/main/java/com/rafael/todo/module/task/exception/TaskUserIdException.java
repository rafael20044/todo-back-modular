package com.rafael.todo.module.task.exception;

import com.rafael.todo.shared.DomainException;

public final class TaskUserIdException extends DomainException {

    private static final String CODE_USER_ID_REQUIRED = "task.user.id.required";
    private static final String MSG_USER_ID_REQUIRED = "User ID for the task cannot be null";

    private static final String CODE_USER_ID_INVALID_FORMAT = "task.user.id.invalid.format";
    private static final String MSG_USER_ID_INVALID_FORMAT = "User ID format for the task is invalid";

    private TaskUserIdException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static TaskUserIdException required() {
        return new TaskUserIdException(CODE_USER_ID_REQUIRED, MSG_USER_ID_REQUIRED, 400);
    }

    public static TaskUserIdException invalidFormat() {
        return new TaskUserIdException(CODE_USER_ID_INVALID_FORMAT, MSG_USER_ID_INVALID_FORMAT, 400);
    }

}
