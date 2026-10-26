package com.rafael.todo.module.task.service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateTaskDtoService(
    UUID userId,
    Long categoryId,
    String title,
    String description,
    String priority,
    LocalDateTime dueDate
) {
}
