package com.rafael.todo.module.task.controller.dto;

import java.time.LocalDateTime;

public record UpdateTaskDto(
    Long categoryId,
    String title,
    String description,
    String status,
    String priority,
    LocalDateTime dueDate
) {
}

