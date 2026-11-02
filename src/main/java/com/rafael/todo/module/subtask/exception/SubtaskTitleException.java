package com.rafael.todo.module.subtask.exception;

import com.rafael.todo.shared.DomainException;
import com.rafael.todo.shared.MyConst;

public final class SubtaskTitleException extends DomainException {

    private static final String CODE_TITLE_REQUIRED = "subtask.title.required";
    private static final String MSG_TITLE_REQUIRED = "Subtask title cannot be null or empty";

    private static final String CODE_TITLE_TOO_SHORT = "subtask.title.too.short";
    private static final String MSG_TITLE_TOO_SHORT = "Subtask title must have at least " + MyConst.MIN_SUBTASK_TITLE_LENGTH.getValue() + " characters";

    private static final String CODE_TITLE_TOO_LONG = "subtask.title.too.long";
    private static final String MSG_TITLE_TOO_LONG = "Subtask title cannot exceed " + MyConst.MAX_SUBTASK_TITLE_LENGTH.getValue() + " characters";

    private SubtaskTitleException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static SubtaskTitleException required() {
        return new SubtaskTitleException(CODE_TITLE_REQUIRED, MSG_TITLE_REQUIRED, 400);
    }

    public static SubtaskTitleException tooShort() {
        return new SubtaskTitleException(CODE_TITLE_TOO_SHORT, MSG_TITLE_TOO_SHORT, 400);
    }

    public static SubtaskTitleException tooLong() {
        return new SubtaskTitleException(CODE_TITLE_TOO_LONG, MSG_TITLE_TOO_LONG, 400);
    }

}

