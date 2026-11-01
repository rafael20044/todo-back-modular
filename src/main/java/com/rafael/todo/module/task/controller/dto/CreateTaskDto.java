package com.rafael.todo.module.task.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateTaskDto(
    UUID userId,
    Long categoryId,
    String title,
    String description,
    String priority,
    LocalDateTime dueDate
) {
}

