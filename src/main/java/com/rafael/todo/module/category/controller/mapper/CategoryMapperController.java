package com.rafael.todo.module.category.controller.mapper;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.rafael.todo.module.category.controller.dto.CreateCategoryDto;
import com.rafael.todo.module.category.controller.dto.ResponseCategoryDto;
import com.rafael.todo.module.category.controller.dto.UpdateCategoryDto;
import com.rafael.todo.module.category.model.CategoryModel;
import com.rafael.todo.module.category.service.dto.CreateCategoryDtoService;
import com.rafael.todo.module.category.service.dto.UpdateCategoryDtoService;

public final class CategoryMapperController {

    public CategoryMapperController() {}

    public static CreateCategoryDtoService toServiceDto(CreateCategoryDto dto) {
        return new CreateCategoryDtoService(dto.userId(), dto.name(), dto.color());
    }

    public static UpdateCategoryDtoService toServiceDto(Long id, UpdateCategoryDto dto) {
        return new UpdateCategoryDtoService(id, dto.name(), dto.color());
    }

    public static ResponseCategoryDto toResponseDto(CategoryModel model) {
        return new ResponseCategoryDto(
            model.getIdValue(),
            model.getUserIdValue(),
            model.getNameValue(),
            model.getColorValue(),
            model.getCreatedAt()
        );
    }

    public static List<ResponseCategoryDto> toResponseList(List<CategoryModel> models) {
        return models.stream().map(CategoryMapperController::toResponseDto).toList();
    }

    public static Page<ResponseCategoryDto> toPage(List<CategoryModel> categories, int page, int size) {
        var pageable = PageRequest.of(page, size);
        var pages = new PageImpl<>(categories, pageable, categories.size());
        return pages.map(CategoryMapperController::toResponseDto);
    }

}
