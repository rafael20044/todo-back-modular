package com.rafael.todo.module.auth.service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateRefreshTokenDtoService(
    UUID userId,
    String tokenHash,
    LocalDateTime expiresAt
) {
}

