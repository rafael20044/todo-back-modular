package com.rafael.todo.module.category.controller.dto;

import java.util.UUID;

public record CreateCategoryDto(
    UUID userId,
    String name,
    String color
) {
}
