package com.rafael.todo.module.task.model.vo;

import com.rafael.todo.module.task.exception.TaskDescriptionException;
import com.rafael.todo.shared.MyConst;

public record TaskDescription(String value) {

    public TaskDescription {
        if (value != null) {
            value = value.trim();
            int max = Integer.parseInt(MyConst.MAX_TASK_DESCRIPTION_LENGTH.getValue());
            if (value.length() > max) {
                throw TaskDescriptionException.tooLong();
            }
            if (value.isEmpty()) {
                value = null;
            }
        }
    }

    public static TaskDescription of(String value) {
        return new TaskDescription(value);
    }

    public static TaskDescription empty() {
        return new TaskDescription(null);
    }

    @Override
    public String toString() {
        return value != null ? value : "";
    }

}
