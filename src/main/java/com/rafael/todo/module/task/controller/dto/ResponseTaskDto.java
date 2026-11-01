package com.rafael.todo.module.task.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseTaskDto(
    Long id,
    UUID userId,
    Long categoryId,
    String title,
    String description,
    String status,
    String priority,
    LocalDateTime dueDate,
    LocalDateTime completedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

