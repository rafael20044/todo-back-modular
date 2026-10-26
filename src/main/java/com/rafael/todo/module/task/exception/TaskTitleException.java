package com.rafael.todo.module.task.exception;

import com.rafael.todo.shared.DomainException;
import com.rafael.todo.shared.MyConst;

public final class TaskTitleException extends DomainException {

    private static final String CODE_TITLE_REQUIRED = "task.title.required";
    private static final String MSG_TITLE_REQUIRED = "Task title cannot be null or empty";

    private static final String CODE_TITLE_TOO_SHORT = "task.title.too.short";
    private static final String MSG_TITLE_TOO_SHORT = "Task title must have at least " + MyConst.MIN_TASK_TITLE_LENGTH.getValue() + " characters";

    private static final String CODE_TITLE_TOO_LONG = "task.title.too.long";
    private static final String MSG_TITLE_TOO_LONG = "Task title cannot exceed " + MyConst.MAX_TASK_TITLE_LENGTH.getValue() + " characters";

    private TaskTitleException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static TaskTitleException required() {
        return new TaskTitleException(CODE_TITLE_REQUIRED, MSG_TITLE_REQUIRED, 400);
    }

    public static TaskTitleException tooShort() {
        return new TaskTitleException(CODE_TITLE_TOO_SHORT, MSG_TITLE_TOO_SHORT, 400);
    }

    public static TaskTitleException tooLong() {
        return new TaskTitleException(CODE_TITLE_TOO_LONG, MSG_TITLE_TOO_LONG, 400);
    }

}
