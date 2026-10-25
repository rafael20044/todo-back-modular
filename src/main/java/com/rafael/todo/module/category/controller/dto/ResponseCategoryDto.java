package com.rafael.todo.module.category.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseCategoryDto(
    Long id,
    UUID userId,
    String name,
    String color,
    LocalDateTime createdAt
) {
}
