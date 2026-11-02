package com.rafael.todo.module.subtask.exception;

import com.rafael.todo.shared.DomainException;

public final class SubtaskIdException extends DomainException {

    private static final String CODE_ID_NOT_FOUND = "subtask.id.not.found";
    private static final String MSG_ID_NOT_FOUND = "Subtask with the specified ID was not found";

    private static final String CODE_ID_REQUIRED = "subtask.id.required";
    private static final String MSG_ID_REQUIRED = "Subtask ID cannot be null";

    private static final String CODE_ID_INVALID = "subtask.id.invalid";
    private static final String MSG_ID_INVALID = "Subtask ID must be greater than zero";

    private SubtaskIdException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static SubtaskIdException notFound() {
        return new SubtaskIdException(CODE_ID_NOT_FOUND, MSG_ID_NOT_FOUND, 404);
    }

    public static SubtaskIdException required() {
        return new SubtaskIdException(CODE_ID_REQUIRED, MSG_ID_REQUIRED, 400);
    }

    public static SubtaskIdException invalid() {
        return new SubtaskIdException(CODE_ID_INVALID, MSG_ID_INVALID, 400);
    }

}

