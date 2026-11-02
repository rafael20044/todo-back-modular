package com.rafael.todo.module.subtask.controller.dto;

import java.time.LocalDateTime;

public record ResponseSubtaskDto(
    Long id,
    Long taskId,
    String title,
    boolean isCompleted,
    Integer position,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

