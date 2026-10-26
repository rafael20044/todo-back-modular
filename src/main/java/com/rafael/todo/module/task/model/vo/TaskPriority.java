package com.rafael.todo.module.task.model.vo;

import com.rafael.todo.module.task.exception.TaskPriorityException;
import com.rafael.todo.module.task.model.enums.TaskPriorityEnum;

public record TaskPriority(TaskPriorityEnum value) {

    public TaskPriority {
        if (value == null) {
            throw TaskPriorityException.required();
        }
    }

    public static TaskPriority of(TaskPriorityEnum value) {
        return new TaskPriority(value);
    }

    public static TaskPriority of(String priorityName) {
        if (priorityName == null || priorityName.trim().isBlank()) {
            throw TaskPriorityException.required();
        }
        try {
            return new TaskPriority(TaskPriorityEnum.valueOf(priorityName.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw TaskPriorityException.invalidPriority();
        }
    }

    public static TaskPriority low() {
        return new TaskPriority(TaskPriorityEnum.LOW);
    }

    public static TaskPriority medium() {
        return new TaskPriority(TaskPriorityEnum.MEDIUM);
    }

    public static TaskPriority high() {
        return new TaskPriority(TaskPriorityEnum.HIGH);
    }

    public static TaskPriority urgent() {
        return new TaskPriority(TaskPriorityEnum.URGENT);
    }

    @Override
    public String toString() {
        return value.getValue();
    }

}
