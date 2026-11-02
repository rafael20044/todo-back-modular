package com.rafael.todo.module.subtask.controller.dto;

public record UpdateSubtaskDto(
    String title,
    Boolean isCompleted,
    Integer position
) {
}

