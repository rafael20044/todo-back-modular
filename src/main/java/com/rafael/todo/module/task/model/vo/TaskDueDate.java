package com.rafael.todo.module.task.model.vo;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public record TaskDueDate(LocalDateTime value) {

    public static TaskDueDate of(LocalDateTime value) {
        return new TaskDueDate(value);
    }

    public static TaskDueDate of(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.trim().isBlank()) {
            return new TaskDueDate(null);
        }
        try {
            return new TaskDueDate(LocalDateTime.parse(isoDateTime.trim()));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date-time format: " + isoDateTime);
        }
    }

    public static TaskDueDate empty() {
        return new TaskDueDate(null);
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "";
    }

}
