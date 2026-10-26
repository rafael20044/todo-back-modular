package com.rafael.todo.module.task.model.enums;

import lombok.Getter;

@Getter
public enum TaskStatusEnum {
    PENDING("PENDING"),
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED");

    private final String value;

    TaskStatusEnum(String value) {
        this.value = value;
    }
}
