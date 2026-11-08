package com.rafael.todo.module.category.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import com.rafael.todo.module.category.controller.dto.CreateCategoryDto;
import com.rafael.todo.module.category.controller.dto.ResponseCategoryDto;
import com.rafael.todo.module.category.controller.dto.UpdateCategoryDto;
import com.rafael.todo.module.category.controller.mapper.CategoryMapperController;
import com.rafael.todo.module.category.model.CategoryModel;
import com.rafael.todo.module.category.model.vo.CategoryColor;
import com.rafael.todo.module.category.model.vo.CategoryId;
import com.rafael.todo.module.category.model.vo.CategoryName;
import com.rafael.todo.module.category.model.vo.CategoryUserId;
import com.rafael.todo.module.category.repository.entity.CategoryEntity;
import com.rafael.todo.module.category.repository.mapper.CategoryMapperRepository;
import com.rafael.todo.module.category.service.dto.CreateCategoryDtoService;
import com.rafael.todo.module.category.service.dto.UpdateCategoryDtoService;
import com.rafael.todo.module.category.service.mapper.CategoryMapperService;

@DisplayName("Category Mappers (Controller, Service, Repository) - Unit Tests")
class CategoryMappersTest {

    @Test
    @DisplayName("CategoryMapperController should map DTOs, lists and pages accurately")
    void shouldMapInCategoryMapperController() {
        UUID userId = UUID.randomUUID();
        CreateCategoryDto createDto = new CreateCategoryDto(userId, "Trabajo", "#3B82F6");
        CreateCategoryDtoService serviceCreateDto = CategoryMapperController.toServiceDto(createDto);

        assertEquals(userId, serviceCreateDto.userId());
        assertEquals("Trabajo", serviceCreateDto.name());
        assertEquals("#3B82F6", serviceCreateDto.color());

        UpdateCategoryDto updateDto = new UpdateCategoryDto("Estudio", "#10B981");
        UpdateCategoryDtoService serviceUpdateDto = CategoryMapperController.toServiceDto(7L, updateDto);

        assertEquals(7L, serviceUpdateDto.id());
        assertEquals("Estudio", serviceUpdateDto.name());
        assertEquals("#10B981", serviceUpdateDto.color());

        LocalDateTime createdAt = LocalDateTime.of(2026, 4, 1, 10, 0);
        CategoryModel model = CategoryModel.reconstitute(
                CategoryId.of(7L),
                CategoryUserId.of(userId),
                CategoryName.of("Estudio"),
                CategoryColor.of("#10B981"),
                createdAt
        );

        ResponseCategoryDto responseDto = CategoryMapperController.toResponseDto(model);
        assertEquals(7L, responseDto.id());
        assertEquals(userId, responseDto.userId());
        assertEquals("Estudio", responseDto.name());
        assertEquals("#10B981", responseDto.color());
        assertEquals(createdAt, responseDto.createdAt());

        List<ResponseCategoryDto> responseList = CategoryMapperController.toResponseList(List.of(model));
        assertEquals(1, responseList.size());
        assertEquals("Estudio", responseList.getFirst().name());

        Page<ResponseCategoryDto> page = CategoryMapperController.toPage(List.of(model), 0, 20);
        assertEquals(1, page.getTotalElements());
        assertEquals(7L, page.getContent().getFirst().id());
    }

    @Test
    @DisplayName("CategoryMapperService should map CreateCategoryDtoService with default or custom color")
    void shouldMapInCategoryMapperService() {
        UUID userId = UUID.randomUUID();

        CreateCategoryDtoService withNullColor = new CreateCategoryDtoService(userId, "Hogar", null);
        CategoryModel modelDefaultColor = CategoryMapperService.toCategoryModel(withNullColor);
        assertNull(modelDefaultColor.getIdValue());
        assertEquals(userId, modelDefaultColor.getUserIdValue());
        assertEquals("Hogar", modelDefaultColor.getNameValue());
        assertEquals("#3B82F6", modelDefaultColor.getColorValue());

        CreateCategoryDtoService withBlankColor = new CreateCategoryDtoService(userId, "Hogar", "   ");
        CategoryModel modelBlankColor = CategoryMapperService.toCategoryModel(withBlankColor);
        assertEquals("#3B82F6", modelBlankColor.getColorValue());

        CreateCategoryDtoService withCustomColor = new CreateCategoryDtoService(userId, "Salud", "#ef4444");
        CategoryModel modelCustomColor = CategoryMapperService.toCategoryModel(withCustomColor);
        assertEquals("#EF4444", modelCustomColor.getColorValue());
    }

    @Test
    @DisplayName("CategoryMapperRepository should map bidirectionally between CategoryModel and CategoryEntity")
    void shouldMapBidirectionallyInCategoryMapperRepository() {
        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 15, 8, 0);

        CategoryModel model = CategoryModel.reconstitute(
                CategoryId.of(12L),
                CategoryUserId.of(userId),
                CategoryName.of("Finanzas"),
                CategoryColor.of("#10B981"),
                createdAt
        );

        CategoryEntity entity = CategoryMapperRepository.toEntity(model);
        assertEquals(12L, entity.getId());
        assertEquals(userId, entity.getUserId());
        assertEquals("Finanzas", entity.getName());
        assertEquals("#10B981", entity.getColor());
        assertEquals(createdAt, entity.getCreatedAt());

        CategoryModel reconstituted = CategoryMapperRepository.toModel(entity);
        assertEquals(12L, reconstituted.getIdValue());
        assertEquals(userId, reconstituted.getUserIdValue());
        assertEquals("Finanzas", reconstituted.getNameValue());
        assertEquals("#10B981", reconstituted.getColorValue());
        assertEquals(createdAt, reconstituted.getCreatedAt());

        entity.setColor(null);
        CategoryModel withNullEntityColor = CategoryMapperRepository.toModel(entity);
        assertEquals("#3B82F6", withNullEntityColor.getColorValue());
    }
}

