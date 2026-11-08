package com.rafael.todo.module.category.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rafael.todo.module.category.model.vo.CategoryColor;
import com.rafael.todo.module.category.model.vo.CategoryId;
import com.rafael.todo.module.category.model.vo.CategoryName;
import com.rafael.todo.module.category.model.vo.CategoryUserId;

@DisplayName("CategoryModel - Domain Model Unit Tests")
class CategoryModelTest {

    @Test
    @DisplayName("Should create new CategoryModel with null ID and default color #3B82F6")
    void shouldCreateNewCategoryModelWithDefaultColor() {
        UUID userId = UUID.randomUUID();
        CategoryModel category = CategoryModel.create(
                CategoryUserId.of(userId),
                CategoryName.of("Personal")
        );

        assertNull(category.getId());
        assertNull(category.getIdValue());
        assertEquals(userId, category.getUserIdValue());
        assertEquals("Personal", category.getNameValue());
        assertEquals("#3B82F6", category.getColorValue());
        assertNotNull(category.getCreatedAt());
    }

    @Test
    @DisplayName("Should create new CategoryModel with custom color")
    void shouldCreateNewCategoryModelWithCustomColor() {
        UUID userId = UUID.randomUUID();
        CategoryModel category = CategoryModel.create(
                CategoryUserId.of(userId),
                CategoryName.of("Urgente"),
                CategoryColor.of("#EF4444")
        );

        assertNull(category.getIdValue());
        assertEquals(userId, category.getUserIdValue());
        assertEquals("Urgente", category.getNameValue());
        assertEquals("#EF4444", category.getColorValue());
    }

    @Test
    @DisplayName("Should reconstitute CategoryModel preserving ID and createdAt")
    void shouldReconstituteCategoryModel() {
        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2026, 3, 10, 9, 30);

        CategoryModel reconstituted = CategoryModel.reconstitute(
                CategoryId.of(5L),
                CategoryUserId.of(userId),
                CategoryName.of("Trabajo"),
                CategoryColor.of("#10B981"),
                createdAt
        );

        assertEquals(5L, reconstituted.getIdValue());
        assertEquals(userId, reconstituted.getUserIdValue());
        assertEquals("Trabajo", reconstituted.getNameValue());
        assertEquals("#10B981", reconstituted.getColorValue());
        assertEquals(createdAt, reconstituted.getCreatedAt());
    }

    @Test
    @DisplayName("Should perform immutable copy-on-write mutations for name and color")
    void shouldPerformImmutableMutations() {
        UUID userId = UUID.randomUUID();
        CategoryModel original = CategoryModel.reconstitute(
                CategoryId.of(1L),
                CategoryUserId.of(userId),
                CategoryName.of("Original"),
                CategoryColor.defaultColor(),
                LocalDateTime.now()
        );

        CategoryModel renamed = original.changeName(CategoryName.of("Renombrada"));
        assertNotSame(original, renamed);
        assertEquals("Original", original.getNameValue());
        assertEquals("Renombrada", renamed.getNameValue());
        assertEquals(original.getIdValue(), renamed.getIdValue());
        assertEquals(original.getColorValue(), renamed.getColorValue());

        CategoryModel recolored = renamed.changeColor(CategoryColor.of("#F59E0B"));
        assertNotSame(renamed, recolored);
        assertEquals("#3B82F6", renamed.getColorValue());
        assertEquals("#F59E0B", recolored.getColorValue());
        assertEquals("Renombrada", recolored.getNameValue());
    }
}

