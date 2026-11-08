package com.rafael.todo.module.user.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import com.rafael.todo.module.user.controller.dto.CreateUserDto;
import com.rafael.todo.module.user.controller.dto.ResponseUserDto;
import com.rafael.todo.module.user.controller.dto.UpdateUserDto;
import com.rafael.todo.module.user.controller.mapper.UserMapperController;
import com.rafael.todo.module.user.model.UserModel;
import com.rafael.todo.module.user.model.enums.UserRoleEnum;
import com.rafael.todo.module.user.model.vo.UserEmail;
import com.rafael.todo.module.user.model.vo.UserName;
import com.rafael.todo.module.user.model.vo.UserPassword;
import com.rafael.todo.module.user.repository.entity.UserEntity;
import com.rafael.todo.module.user.repository.mapper.UserMapperRepository;
import com.rafael.todo.module.user.service.dto.CreateUserDtoService;
import com.rafael.todo.module.user.service.dto.UpdateUserDtoService;
import com.rafael.todo.module.user.service.mapper.UserMapperService;

@DisplayName("User Mappers (Controller, Service, Repository) - Unit Tests")
class UserMappersTest {

    @Test
    @DisplayName("UserMapperController should map DTOs and Page accurately")
    void shouldMapInUserMapperController() {
        CreateUserDto createDto = new CreateUserDto("Rafael", "rafael@example.com", "Password123", "ROLE_USER");
        CreateUserDtoService serviceCreateDto = UserMapperController.toServiceDto(createDto);

        assertEquals("Rafael", serviceCreateDto.name());
        assertEquals("rafael@example.com", serviceCreateDto.email());
        assertEquals("Password123", serviceCreateDto.password());
        assertEquals("ROLE_USER", serviceCreateDto.role());

        UUID uuid = UUID.randomUUID();
        UpdateUserDto updateDto = new UpdateUserDto("NewName", "NewPass123", "ROLE_ADMIN");
        UpdateUserDtoService serviceUpdateDto = UserMapperController.toServiceDto(uuid, updateDto);

        assertEquals(uuid, serviceUpdateDto.uuid());
        assertEquals("NewName", serviceUpdateDto.name());
        assertEquals("NewPass123", serviceUpdateDto.password());
        assertEquals("ROLE_ADMIN", serviceUpdateDto.role());

        UserModel model = UserModel.create(
                UserName.of("Rafael"),
                UserEmail.of("rafael@example.com"),
                UserPassword.fromRaw("Password123")
        );

        ResponseUserDto responseDto = UserMapperController.toResponseDto(model);
        assertEquals(model.getIdValue(), responseDto.id());
        assertEquals("Rafael", responseDto.username());
        assertEquals("rafael@example.com", responseDto.email());
        assertEquals("ROLE_USER", responseDto.role());
        assertEquals(model.getCreatedAt(), responseDto.createdAt());
        assertEquals(model.getUpdatedAt(), responseDto.updatedAt());

        Page<ResponseUserDto> page = UserMapperController.toPage(List.of(model), 0, 10);
        assertEquals(1, page.getTotalElements());
        assertEquals("Rafael", page.getContent().getFirst().username());
    }

    @Test
    @DisplayName("UserMapperService should map CreateUserDtoService to UserModel with default and explicit role")
    void shouldMapInUserMapperService() {
        CreateUserDtoService withoutRole = new CreateUserDtoService("Rafael", "rafael@example.com", "Password123", null);
        UserModel defaultRoleModel = UserMapperService.toUserModel(withoutRole);

        assertEquals("Rafael", defaultRoleModel.getNameValue());
        assertEquals("rafael@example.com", defaultRoleModel.getEmailValue());
        assertEquals(UserRoleEnum.ROLE_USER, defaultRoleModel.getRoleValue());

        CreateUserDtoService withAdminRole = new CreateUserDtoService("Admin", "admin@example.com", "Password123", "ROLE_ADMIN");
        UserModel adminModel = UserMapperService.toUserModel(withAdminRole);

        assertEquals(UserRoleEnum.ROLE_ADMIN, adminModel.getRoleValue());
    }

    @Test
    @DisplayName("UserMapperRepository should map bidirectionally between UserModel and UserEntity")
    void shouldMapBidirectionallyInUserMapperRepository() {
        UserModel originalModel = UserModel.create(
                UserName.of("Rafael"),
                UserEmail.of("rafael@example.com"),
                UserPassword.fromHash("$2a$10$hashedValue")
        );

        UserEntity entity = UserMapperRepository.toEntity(originalModel);
        assertNotNull(entity);
        assertEquals(originalModel.getIdValue(), entity.getId());
        assertEquals("Rafael", entity.getName());
        assertEquals("rafael@example.com", entity.getEmail());
        assertEquals("$2a$10$hashedValue", entity.getPasswordHash());
        assertEquals("ROLE_USER", entity.getRole());
        assertTrue(entity.isActive());
        assertEquals(originalModel.getCreatedAt(), entity.getCreatedAt());
        assertEquals(originalModel.getUpdatedAt(), entity.getUpdatedAt());

        LocalDateTime fixedTime = LocalDateTime.of(2026, 5, 1, 12, 0);
        entity.setCreatedAt(fixedTime);
        entity.setUpdatedAt(fixedTime);

        UserModel reconstituted = UserMapperRepository.toModel(entity);
        assertEquals(entity.getId(), reconstituted.getIdValue());
        assertEquals(entity.getName(), reconstituted.getNameValue());
        assertEquals(entity.getEmail(), reconstituted.getEmailValue());
        assertEquals(entity.getPasswordHash(), reconstituted.getPasswordValue());
        assertEquals(UserRoleEnum.ROLE_USER, reconstituted.getRoleValue());
        assertTrue(reconstituted.getAccountValue());
        assertEquals(fixedTime, reconstituted.getCreatedAt());
        assertEquals(fixedTime, reconstituted.getUpdatedAt());
    }
}

