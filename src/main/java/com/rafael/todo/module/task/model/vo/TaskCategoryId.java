package com.rafael.todo.module.task.model.vo;

import com.rafael.todo.module.task.exception.TaskCategoryIdException;

public record TaskCategoryId(Long value) {

    public TaskCategoryId {
        if (value == null || value <= 0) {
            throw TaskCategoryIdException.invalid();
        }
    }

    public static TaskCategoryId of(Long value) {
        return new TaskCategoryId(value);
    }

    public static TaskCategoryId of(String value) {
        if (value == null || value.trim().isBlank()) {
            throw TaskCategoryIdException.invalid();
        }
        try {
            return new TaskCategoryId(Long.parseLong(value.trim()));
        } catch (NumberFormatException e) {
            throw TaskCategoryIdException.invalid();
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
