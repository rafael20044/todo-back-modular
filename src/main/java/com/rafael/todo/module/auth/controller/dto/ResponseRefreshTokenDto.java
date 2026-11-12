package com.rafael.todo.module.auth.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseRefreshTokenDto(
        Long id,
        UUID userId,
        LocalDateTime expiresAt,
        Boolean isRevoked,
        LocalDateTime createdAt
) {
}

