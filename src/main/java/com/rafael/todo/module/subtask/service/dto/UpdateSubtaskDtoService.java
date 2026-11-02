package com.rafael.todo.module.subtask.service.dto;

public record UpdateSubtaskDtoService(
    Long id,
    String title,
    Boolean isCompleted,
    Integer position
) {
}

