package com.rafael.todo.module.category.service.mapper;

import com.rafael.todo.module.category.model.CategoryModel;
import com.rafael.todo.module.category.model.vo.CategoryColor;
import com.rafael.todo.module.category.model.vo.CategoryName;
import com.rafael.todo.module.category.model.vo.CategoryUserId;
import com.rafael.todo.module.category.service.dto.CreateCategoryDtoService;

public final class CategoryMapperService {

    private CategoryMapperService() {}

    public static CategoryModel toCategoryModel(CreateCategoryDtoService createDto) {
        var userId = CategoryUserId.of(createDto.userId());
        var name = CategoryName.of(createDto.name());

        if (createDto.color() == null || createDto.color().trim().isBlank()) {
            return CategoryModel.create(userId, name);
        }

        return CategoryModel.create(userId, name, CategoryColor.of(createDto.color()));
    }

}
