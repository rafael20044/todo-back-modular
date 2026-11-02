package com.rafael.todo.module.subtask.model.vo;

import com.rafael.todo.module.subtask.exception.SubtaskTaskIdException;

public record SubtaskTaskId(Long value) {

    public SubtaskTaskId {
        if (value == null) {
            throw SubtaskTaskIdException.required();
        }
        if (value <= 0) {
            throw SubtaskTaskIdException.invalid();
        }
    }

    public static SubtaskTaskId of(Long value) {
        return new SubtaskTaskId(value);
    }

    public static SubtaskTaskId of(String value) {
        if (value == null || value.trim().isBlank()) {
            throw SubtaskTaskIdException.required();
        }
        try {
            return new SubtaskTaskId(Long.parseLong(value.trim()));
        } catch (NumberFormatException e) {
            throw SubtaskTaskIdException.invalid();
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }

}

