package com.rafael.todo.module.task.model.vo;

import java.util.UUID;

import com.rafael.todo.module.task.exception.TaskUserIdException;

public record TaskUserId(UUID value) {

    public TaskUserId {
        if (value == null) {
            throw TaskUserIdException.required();
        }
    }

    public static TaskUserId of(UUID value) {
        return new TaskUserId(value);
    }

    public static TaskUserId of(String value) {
        if (value == null || value.trim().isBlank()) {
            throw TaskUserIdException.required();
        }
        try {
            return new TaskUserId(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException e) {
            throw TaskUserIdException.invalidFormat();
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
