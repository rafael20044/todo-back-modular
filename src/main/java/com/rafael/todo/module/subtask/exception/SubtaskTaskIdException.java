package com.rafael.todo.module.subtask.exception;

import com.rafael.todo.shared.DomainException;

public final class SubtaskTaskIdException extends DomainException {

    private static final String CODE_TASK_ID_REQUIRED = "subtask.task.id.required";
    private static final String MSG_TASK_ID_REQUIRED = "Task ID for the subtask cannot be null";

    private static final String CODE_TASK_ID_INVALID = "subtask.task.id.invalid";
    private static final String MSG_TASK_ID_INVALID = "Task ID for the subtask must be greater than zero";

    private SubtaskTaskIdException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static SubtaskTaskIdException required() {
        return new SubtaskTaskIdException(CODE_TASK_ID_REQUIRED, MSG_TASK_ID_REQUIRED, 400);
    }

    public static SubtaskTaskIdException invalid() {
        return new SubtaskTaskIdException(CODE_TASK_ID_INVALID, MSG_TASK_ID_INVALID, 400);
    }

}

