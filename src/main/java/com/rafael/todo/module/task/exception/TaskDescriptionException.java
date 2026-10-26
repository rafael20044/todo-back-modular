package com.rafael.todo.module.task.exception;

import com.rafael.todo.shared.DomainException;
import com.rafael.todo.shared.MyConst;

public final class TaskDescriptionException extends DomainException {

    private static final String CODE_DESCRIPTION_TOO_LONG = "task.description.too.long";
    private static final String MSG_DESCRIPTION_TOO_LONG = "Task description cannot exceed " + MyConst.MAX_TASK_DESCRIPTION_LENGTH.getValue() + " characters";

    private TaskDescriptionException(String code, String message, int statusCode) {
        super(code, message, statusCode);
    }

    public static TaskDescriptionException tooLong() {
        return new TaskDescriptionException(CODE_DESCRIPTION_TOO_LONG, MSG_DESCRIPTION_TOO_LONG, 400);
    }

}
