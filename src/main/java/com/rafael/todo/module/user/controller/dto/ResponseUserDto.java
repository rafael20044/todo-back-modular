package com.rafael.todo.module.user.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseUserDto(
    UUID id,
    String username,
    String email,
    String role,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}
