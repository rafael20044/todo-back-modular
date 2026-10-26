package com.rafael.todo.module.task.service.dto;

import java.time.LocalDateTime;

public record UpdateTaskDtoService(
    Long id,
    Long categoryId,
    String title,
    String description,
    String status,
    String priority,
    LocalDateTime dueDate
) {
}
