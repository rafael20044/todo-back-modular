package com.rafael.todo.module.task.model.vo;

import com.rafael.todo.module.task.exception.TaskIdException;

public record TaskId(Long value) {

    public TaskId {
        if (value == null) {
            throw TaskIdException.required();
        }
        if (value <= 0) {
            throw TaskIdException.invalid();
        }
    }

    public static TaskId of(Long value) {
        return new TaskId(value);
    }

    public static TaskId of(String value) {
        if (value == null || value.trim().isBlank()) {
            throw TaskIdException.required();
        }
        try {
            return new TaskId(Long.parseLong(value.trim()));
        } catch (NumberFormatException e) {
            throw TaskIdException.invalid();
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
