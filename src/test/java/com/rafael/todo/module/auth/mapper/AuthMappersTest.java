package com.rafael.todo.module.auth.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rafael.todo.module.auth.controller.dto.LoginDto;
import com.rafael.todo.module.auth.controller.mapper.AuthMapperController;
import com.rafael.todo.module.auth.model.RefreshTokenModel;
import com.rafael.todo.module.auth.model.vo.RefreshTokenExpiration;
import com.rafael.todo.module.auth.model.vo.RefreshTokenHash;
import com.rafael.todo.module.auth.model.vo.RefreshTokenId;
import com.rafael.todo.module.auth.model.vo.RefreshTokenRevoked;
import com.rafael.todo.module.auth.model.vo.RefreshTokenUserId;
import com.rafael.todo.module.auth.repository.entity.RefreshTokenEntity;
import com.rafael.todo.module.auth.repository.mapper.RefreshTokenMapperRepository;
import com.rafael.todo.module.auth.service.dto.AuthTokensDtoService;
import com.rafael.todo.module.auth.service.dto.CreateRefreshTokenDtoService;
import com.rafael.todo.module.auth.service.mapper.RefreshTokenMapperService;

@DisplayName("Auth Mappers Tests (Controller, Service, Repository)")
class AuthMappersTest {

    @Test
    @DisplayName("RefreshTokenMapperRepository should map Model to Entity and Entity to Model")
    void repositoryMapperShouldMapBidirectionally() {
        var userId = UUID.randomUUID();
        var createdAt = LocalDateTime.now().minusHours(2);
        var expiresAt = LocalDateTime.now().plusDays(7);

        var model = RefreshTokenModel.reconstitute(
                RefreshTokenId.of(15L),
                RefreshTokenUserId.of(userId),
                RefreshTokenHash.of("hash-abc"),
                RefreshTokenExpiration.of(expiresAt),
                RefreshTokenRevoked.of(true),
                createdAt
        );

        RefreshTokenEntity entity = RefreshTokenMapperRepository.toEntity(model);
        assertEquals(15L, entity.getId());
        assertEquals(userId, entity.getUserId());
        assertEquals("hash-abc", entity.getTokenHash());
        assertEquals(expiresAt, entity.getExpiresAt());
        assertTrue(entity.isRevoked());
        assertEquals(createdAt, entity.getCreatedAt());

        RefreshTokenModel mappedBack = RefreshTokenMapperRepository.toModel(entity);
        assertEquals(15L, mappedBack.getIdValue());
        assertEquals(userId, mappedBack.getUserIdValue());
        assertEquals("hash-abc", mappedBack.getTokenHashValue());
        assertEquals(expiresAt, mappedBack.getExpiresAtValue());
        assertTrue(mappedBack.isRevokedValue());
        assertEquals(createdAt, mappedBack.getCreatedAt());
    }

    @Test
    @DisplayName("RefreshTokenMapperService should map CreateRefreshTokenDtoService to RefreshTokenModel")
    void serviceMapperShouldMapToModel() {
        var userId = UUID.randomUUID();
        var expiresAt = LocalDateTime.now().plusDays(7);
        var dto = new CreateRefreshTokenDtoService(userId, "token-xyz", expiresAt);

        var model = RefreshTokenMapperService.toRefreshTokenModel(dto);

        assertEquals(userId, model.getUserIdValue());
        assertEquals("token-xyz", model.getTokenHashValue());
        assertEquals(expiresAt, model.getExpiresAtValue());
        assertFalse(model.isRevokedValue());
        assertNotNull(model.getCreatedAt());
    }

    @Test
    @DisplayName("AuthMapperController should map LoginDto, refreshToken, AuthTokensDtoService and RefreshTokenModel")
    void controllerMapperShouldMapAllDtos() {
        var loginDto = new LoginDto("rafael@gmail.com", "secret123");
        var loginServiceDto = AuthMapperController.toServiceDto(loginDto);
        assertEquals("rafael@gmail.com", loginServiceDto.email());
        assertEquals("secret123", loginServiceDto.password());

        var refreshServiceDto = AuthMapperController.toServiceDto("refresh-token-value");
        assertEquals("refresh-token-value", refreshServiceDto.refreshToken());

        var authTokensDto = new AuthTokensDtoService("jwt-access-token", "refresh-token-value", LocalDateTime.now().plusDays(7));
        var responseAuthDto = AuthMapperController.toResponseDto(authTokensDto);
        assertEquals("jwt-access-token", responseAuthDto.accessToken());

        var userId = UUID.randomUUID();
        var createdAt = LocalDateTime.now();
        var expiresAt = LocalDateTime.now().plusDays(7);
        var model = RefreshTokenModel.reconstitute(
                RefreshTokenId.of(99L),
                RefreshTokenUserId.of(userId),
                RefreshTokenHash.of("hash-99"),
                RefreshTokenExpiration.of(expiresAt),
                RefreshTokenRevoked.active(),
                createdAt
        );

        var responseRefreshTokenDto = AuthMapperController.toResponseDto(model);
        assertEquals(99L, responseRefreshTokenDto.id());
        assertEquals(userId, responseRefreshTokenDto.userId());
        assertEquals(expiresAt, responseRefreshTokenDto.expiresAt());
        assertFalse(responseRefreshTokenDto.isRevoked());
        assertEquals(createdAt, responseRefreshTokenDto.createdAt());

        var page = AuthMapperController.toPage(List.of(model), 0, 10);
        assertEquals(1, page.getTotalElements());
        assertEquals(99L, page.getContent().get(0).id());
    }
}

