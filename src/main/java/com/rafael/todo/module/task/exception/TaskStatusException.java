package com.rafael.todo.module.task.exception;

import com.rafael.todo.shared.DomainException;

public final class TaskStatusException extends DomainException {

    private static final String CODE_STATUS_REQUIRED = "task.status.required";
    private static final String MSG_STATUS_REQUIRED = "Task status cannot be null or empty";

    private static final String CODE_STATUS_INVALID = "task.status.invalid";
    private static final String MSG_STATUS_INVALID = "Invalid task status provided";

    private TaskStatusException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static TaskStatusException required() {
        return new TaskStatusException(CODE_STATUS_REQUIRED, MSG_STATUS_REQUIRED, 400);
    }

    public static TaskStatusException invalidStatus() {
        return new TaskStatusException(CODE_STATUS_INVALID, MSG_STATUS_INVALID, 400);
    }

}
