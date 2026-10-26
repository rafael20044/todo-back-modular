package com.rafael.todo.module.task.model.vo;

import com.rafael.todo.module.task.exception.TaskStatusException;
import com.rafael.todo.module.task.model.enums.TaskStatusEnum;

public record TaskStatus(TaskStatusEnum value) {

    public TaskStatus {
        if (value == null) {
            throw TaskStatusException.required();
        }
    }

    public static TaskStatus of(TaskStatusEnum value) {
        return new TaskStatus(value);
    }

    public static TaskStatus of(String statusName) {
        if (statusName == null || statusName.trim().isBlank()) {
            throw TaskStatusException.required();
        }
        try {
            return new TaskStatus(TaskStatusEnum.valueOf(statusName.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw TaskStatusException.invalidStatus();
        }
    }

    public static TaskStatus pending() {
        return new TaskStatus(TaskStatusEnum.PENDING);
    }

    public static TaskStatus inProgress() {
        return new TaskStatus(TaskStatusEnum.IN_PROGRESS);
    }

    public static TaskStatus completed() {
        return new TaskStatus(TaskStatusEnum.COMPLETED);
    }

    public static TaskStatus cancelled() {
        return new TaskStatus(TaskStatusEnum.CANCELLED);
    }

    @Override
    public String toString() {
        return value.getValue();
    }

}
