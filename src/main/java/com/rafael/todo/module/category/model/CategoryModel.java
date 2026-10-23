package com.rafael.todo.module.category.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.rafael.todo.module.category.model.vo.CategoryColor;
import com.rafael.todo.module.category.model.vo.CategoryId;
import com.rafael.todo.module.category.model.vo.CategoryName;
import com.rafael.todo.module.category.model.vo.CategoryUserId;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CategoryModel {

    private final CategoryId id;
    private final CategoryUserId userId;
    private final CategoryName name;
    private final CategoryColor color;
    private final LocalDateTime createdAt;

    public static CategoryModel create(CategoryUserId userId, CategoryName name, CategoryColor color) {
        return new CategoryModel(null, userId, name, color, LocalDateTime.now());
    }

    public static CategoryModel create(CategoryUserId userId, CategoryName name) {
        return create(userId, name, CategoryColor.defaultColor());
    }

    public static CategoryModel reconstitute(
            CategoryId id,
            CategoryUserId userId,
            CategoryName name,
            CategoryColor color,
            LocalDateTime createdAt
    ) {
        return new CategoryModel(id, userId, name, color, createdAt);
    }

    public CategoryModel changeName(CategoryName newName) {
        return copy(newName, this.color);
    }

    public CategoryModel changeColor(CategoryColor newColor) {
        return copy(this.name, newColor);
    }

    public Long getIdValue() {
        return id != null ? id.value() : null;
    }

    public UUID getUserIdValue() {
        return userId.value();
    }

    public String getNameValue() {
        return name.value();
    }

    public String getColorValue() {
        return color.value();
    }

    private CategoryModel copy(CategoryName name, CategoryColor color) {
        return new CategoryModel(this.id, this.userId, name, color, this.createdAt);
    }

}
