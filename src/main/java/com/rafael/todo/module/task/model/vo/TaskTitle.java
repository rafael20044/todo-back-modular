package com.rafael.todo.module.task.model.vo;

import com.rafael.todo.module.task.exception.TaskTitleException;
import com.rafael.todo.shared.MyConst;

public record TaskTitle(String value) {

    public TaskTitle {
        if (value == null || value.trim().isBlank()) {
            throw TaskTitleException.required();
        }
        value = value.trim();
        int min = Integer.parseInt(MyConst.MIN_TASK_TITLE_LENGTH.getValue());
        int max = Integer.parseInt(MyConst.MAX_TASK_TITLE_LENGTH.getValue());
        if (value.length() < min) {
            throw TaskTitleException.tooShort();
        }
        if (value.length() > max) {
            throw TaskTitleException.tooLong();
        }
    }

    public static TaskTitle of(String value) {
        return new TaskTitle(value);
    }

    @Override
    public String toString() {
        return value;
    }

}
