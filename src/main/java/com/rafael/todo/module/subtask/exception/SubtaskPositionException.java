package com.rafael.todo.module.subtask.exception;

import com.rafael.todo.shared.DomainException;

public final class SubtaskPositionException extends DomainException {

    private static final String CODE_POSITION_INVALID = "subtask.position.invalid";
    private static final String MSG_POSITION_INVALID = "Subtask position cannot be negative";

    private SubtaskPositionException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static SubtaskPositionException invalid() {
        return new SubtaskPositionException(CODE_POSITION_INVALID, MSG_POSITION_INVALID, 400);
    }

}

