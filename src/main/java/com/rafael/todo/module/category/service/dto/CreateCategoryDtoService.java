package com.rafael.todo.module.category.service.dto;

import java.util.UUID;

public record CreateCategoryDtoService(
    UUID userId,
    String name,
    String color
) {
}
