package com.rafael.todo.module.subtask.model.vo;

import com.rafael.todo.module.subtask.exception.SubtaskIdException;

public record SubtaskId(Long value) {

    public SubtaskId {
        if (value == null) {
            throw SubtaskIdException.required();
        }
        if (value <= 0) {
            throw SubtaskIdException.invalid();
        }
    }

    public static SubtaskId of(Long value) {
        return new SubtaskId(value);
    }

    public static SubtaskId of(String value) {
        if (value == null || value.trim().isBlank()) {
            throw SubtaskIdException.required();
        }
        try {
            return new SubtaskId(Long.parseLong(value.trim()));
        } catch (NumberFormatException e) {
            throw SubtaskIdException.invalid();
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }

}

