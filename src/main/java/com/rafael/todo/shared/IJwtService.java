package com.rafael.todo.shared;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IJwtService {

    String generateAccessToken(UUID userId, String email, String role);

    String generateRefreshToken();

    LocalDateTime calculateRefreshTokenExpiration();

    UUID extractUserId(String token);

    String extractEmail(String token);

    String extractRole(String token);

    boolean isTokenValid(String token);

}

