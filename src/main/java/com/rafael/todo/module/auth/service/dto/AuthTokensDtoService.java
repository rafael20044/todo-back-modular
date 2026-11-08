package com.rafael.todo.module.auth.service.dto;

import java.time.LocalDateTime;

public record AuthTokensDtoService(
    String accessToken,
    String refreshToken,
    LocalDateTime expiresAt
) {
}

