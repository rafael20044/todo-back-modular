package com.rafael.todo.module.category.model.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.rafael.todo.module.category.exception.CategoryColorException;
import com.rafael.todo.module.category.exception.CategoryIdException;
import com.rafael.todo.module.category.exception.CategoryNameException;
import com.rafael.todo.module.category.exception.CategoryUserIdException;

@DisplayName("Category Value Objects - Unit Tests")
class CategoryValueObjectsTest {

    @Nested
    @DisplayName("CategoryId Value Object")
    class CategoryIdTests {

        @Test
        @DisplayName("Should create CategoryId from valid positive Long and String")
        void shouldCreateFromValidLongAndString() {
            CategoryId fromLong = CategoryId.of(10L);
            CategoryId fromString = CategoryId.of("  25  ");

            assertEquals(10L, fromLong.value());
            assertEquals("10", fromLong.toString());
            assertEquals(25L, fromString.value());
        }

        @Test
        @DisplayName("Should throw CategoryIdException when Long value is null")
        void shouldThrowWhenLongIsNull() {
            CategoryIdException ex = assertThrows(CategoryIdException.class, () -> CategoryId.of((Long) null));
            assertEquals("category.id.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -99L })
        @DisplayName("Should throw CategoryIdException when Long value is zero or negative")
        void shouldThrowWhenLongIsZeroOrNegative(long invalidId) {
            CategoryIdException ex = assertThrows(CategoryIdException.class, () -> CategoryId.of(invalidId));
            assertEquals("category.id.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   ", "\t" })
        @DisplayName("Should throw CategoryIdException when String is null or blank")
        void shouldThrowWhenStringIsNullOrBlank(String input) {
            CategoryIdException ex = assertThrows(CategoryIdException.class, () -> CategoryId.of(input));
            assertEquals("category.id.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @ValueSource(strings = { "abc", "12.5", "0", "-5" })
        @DisplayName("Should throw CategoryIdException when String is non-numeric or non-positive")
        void shouldThrowWhenStringIsInvalid(String invalidInput) {
            CategoryIdException ex = assertThrows(CategoryIdException.class, () -> CategoryId.of(invalidInput));
            assertEquals("category.id.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("CategoryUserId Value Object")
    class CategoryUserIdTests {

        @Test
        @DisplayName("Should create CategoryUserId from valid UUID and String")
        void shouldCreateFromValidUuidAndString() {
            UUID rawUuid = UUID.randomUUID();
            CategoryUserId fromUuid = CategoryUserId.of(rawUuid);
            CategoryUserId fromString = CategoryUserId.of("  " + rawUuid + "  ");

            assertEquals(rawUuid, fromUuid.value());
            assertEquals(rawUuid.toString(), fromUuid.toString());
            assertEquals(rawUuid, fromString.value());
        }

        @Test
        @DisplayName("Should throw CategoryUserIdException when UUID is null")
        void shouldThrowWhenUuidIsNull() {
            CategoryUserIdException ex = assertThrows(
                    CategoryUserIdException.class,
                    () -> CategoryUserId.of((UUID) null)
            );
            assertEquals("category.user.id.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   " })
        @DisplayName("Should throw CategoryUserIdException when String is null or blank")
        void shouldThrowWhenStringIsNullOrBlank(String input) {
            CategoryUserIdException ex = assertThrows(
                    CategoryUserIdException.class,
                    () -> CategoryUserId.of(input)
            );
            assertEquals("category.user.id.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw CategoryUserIdException when String has invalid UUID format")
        void shouldThrowWhenStringFormatIsInvalid() {
            CategoryUserIdException ex = assertThrows(
                    CategoryUserIdException.class,
                    () -> CategoryUserId.of("invalid-uuid-value")
            );
            assertEquals("category.user.id.invalid.format", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("CategoryName Value Object")
    class CategoryNameTests {

        @Test
        @DisplayName("Should create CategoryName and trim whitespace")
        void shouldCreateValidCategoryName() {
            CategoryName name = CategoryName.of("  Trabajo  ");
            assertEquals("Trabajo", name.value());
            assertEquals("Trabajo", name.toString());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   ", "\n" })
        @DisplayName("Should throw CategoryNameException when name is null or blank")
        void shouldThrowWhenNameIsNullOrBlank(String input) {
            CategoryNameException ex = assertThrows(CategoryNameException.class, () -> CategoryName.of(input));
            assertEquals("category.name.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw CategoryNameException when name is shorter than 2 characters")
        void shouldThrowWhenNameIsTooShort() {
            CategoryNameException ex = assertThrows(CategoryNameException.class, () -> CategoryName.of("A"));
            assertEquals("category.name.too.short", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw CategoryNameException when name exceeds 50 characters")
        void shouldThrowWhenNameIsTooLong() {
            String longName = "a".repeat(51);
            CategoryNameException ex = assertThrows(CategoryNameException.class, () -> CategoryName.of(longName));
            assertEquals("category.name.too.long", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("CategoryColor Value Object")
    class CategoryColorTests {

        @Test
        @DisplayName("Should create CategoryColor normalizing to uppercase and trimming")
        void shouldCreateValidCategoryColor() {
            CategoryColor color = CategoryColor.of("  #ef4444  ");
            assertEquals("#EF4444", color.value());
            assertEquals("#EF4444", color.toString());
        }

        @Test
        @DisplayName("Should return #3B82F6 as default color")
        void shouldReturnDefaultColor() {
            CategoryColor defaultColor = CategoryColor.defaultColor();
            assertEquals("#3B82F6", defaultColor.value());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   " })
        @DisplayName("Should throw CategoryColorException when color is null or blank")
        void shouldThrowWhenColorIsNullOrBlank(String input) {
            CategoryColorException ex = assertThrows(CategoryColorException.class, () -> CategoryColor.of(input));
            assertEquals("category.color.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @ValueSource(strings = { "3B82F6", "#FFF", "#12345G", "#1234567", "red" })
        @DisplayName("Should throw CategoryColorException when hex color format is invalid")
        void shouldThrowWhenColorFormatIsInvalid(String invalidColor) {
            CategoryColorException ex = assertThrows(CategoryColorException.class, () -> CategoryColor.of(invalidColor));
            assertEquals("category.color.invalid.format", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }
}

