package com.rafael.todo.module.category.repository.mapper;

import com.rafael.todo.module.category.model.CategoryModel;
import com.rafael.todo.module.category.model.vo.CategoryColor;
import com.rafael.todo.module.category.model.vo.CategoryId;
import com.rafael.todo.module.category.model.vo.CategoryName;
import com.rafael.todo.module.category.model.vo.CategoryUserId;
import com.rafael.todo.module.category.repository.entity.CategoryEntity;

public final class CategoryMapperRepository {

    private CategoryMapperRepository() {}

    public static CategoryEntity toEntity(CategoryModel model) {
        return CategoryEntity.builder()
            .id(model.getIdValue())
            .userId(model.getUserIdValue())
            .name(model.getNameValue())
            .color(model.getColorValue())
            .createdAt(model.getCreatedAt())
            .build();
    }

    public static CategoryModel toModel(CategoryEntity entity) {
        return CategoryModel.reconstitute(
            CategoryId.of(entity.getId()),
            CategoryUserId.of(entity.getUserId()),
            CategoryName.of(entity.getName()),
            entity.getColor() != null ? CategoryColor.of(entity.getColor()) : CategoryColor.defaultColor(),
            entity.getCreatedAt()
        );
    }

}
