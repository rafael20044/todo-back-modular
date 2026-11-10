package com.rafael.todo.shared;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rafael.todo.configuration.ApplicationConfiguration;
import com.rafael.todo.module.auth.exception.AuthCredentialsException;

import lombok.extern.java.Log;

@Service
@Log
public final class JwtService implements IJwtService {

    private static final String ISSUER = "todo-back-modular";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLE = "role";

    private final Algorithm algorithm;
    private final long accessTokenExpirationMinutes;
    private final long refreshTokenExpirationDays;

    @Autowired
    public JwtService(ApplicationConfiguration appConfig) {
        this.algorithm = Algorithm.HMAC256(appConfig.getJwtSecret());
        this.accessTokenExpirationMinutes = appConfig.getAccessTokenExpirationMinutes();
        this.refreshTokenExpirationDays = appConfig.getRefreshTokenExpirationDays();
    }

    @Override
    public String generateAccessToken(UUID userId, String email, String role) {
        log.info("Generating access token for user: " + userId);
        Instant now = Instant.now();
        Instant expiresAt = now.plus(accessTokenExpirationMinutes, ChronoUnit.MINUTES);

        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(userId.toString())
                .withClaim(CLAIM_EMAIL, email)
                .withClaim(CLAIM_ROLE, role)
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    @Override
    public String generateRefreshToken() {
        log.info("Generating opaque refresh token");
        return UUID.randomUUID().toString();
    }

    @Override
    public LocalDateTime calculateRefreshTokenExpiration() {
        return LocalDateTime.now().plusDays(refreshTokenExpirationDays);
    }

    @Override
    public UUID extractUserId(String token) {
        DecodedJWT decoded = verifyToken(token);
        return UUID.fromString(decoded.getSubject());
    }

    @Override
    public String extractEmail(String token) {
        DecodedJWT decoded = verifyToken(token);
        return decoded.getClaim(CLAIM_EMAIL).asString();
    }

    @Override
    public String extractRole(String token) {
        DecodedJWT decoded = verifyToken(token);
        return decoded.getClaim(CLAIM_ROLE).asString();
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            verifyToken(token);
            return true;
        } catch (DomainException e) {
            return false;
        }
    }

    private DecodedJWT verifyToken(String token) {
        if (token == null || token.trim().isBlank()) {
            throw AuthCredentialsException.invalidAccessToken();
        }
        try {
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token.trim());
        } catch (TokenExpiredException e) {
            log.warning("JWT access token has expired: " + e.getMessage());
            throw AuthCredentialsException.expiredAccessToken();
        } catch (JWTVerificationException | IllegalArgumentException e) {
            log.warning("Invalid JWT access token: " + e.getMessage());
            throw AuthCredentialsException.invalidAccessToken();
        }
    }

}
