package com.rafael.todo.module.task.exception;

import com.rafael.todo.shared.DomainException;

public final class TaskPriorityException extends DomainException {

    private static final String CODE_PRIORITY_REQUIRED = "task.priority.required";
    private static final String MSG_PRIORITY_REQUIRED = "Task priority cannot be null or empty";

    private static final String CODE_PRIORITY_INVALID = "task.priority.invalid";
    private static final String MSG_PRIORITY_INVALID = "Invalid task priority provided";

    private TaskPriorityException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static TaskPriorityException required() {
        return new TaskPriorityException(CODE_PRIORITY_REQUIRED, MSG_PRIORITY_REQUIRED, 400);
    }

    public static TaskPriorityException invalidPriority() {
        return new TaskPriorityException(CODE_PRIORITY_INVALID, MSG_PRIORITY_INVALID, 400);
    }

}
