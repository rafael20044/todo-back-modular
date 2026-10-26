package com.rafael.todo.module.task.model.enums;

import lombok.Getter;

@Getter
public enum TaskPriorityEnum {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH"),
    URGENT("URGENT");

    private final String value;

    TaskPriorityEnum(String value) {
        this.value = value;
    }
}
