package com.rafael.todo.module.subtask.service.dto;

public record CreateSubtaskDtoService(
    Long taskId,
    String title,
    Integer position
) {
}

