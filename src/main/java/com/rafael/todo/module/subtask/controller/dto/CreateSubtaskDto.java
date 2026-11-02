package com.rafael.todo.module.subtask.controller.dto;

public record CreateSubtaskDto(
    Long taskId,
    String title,
    Integer position
) {
}

