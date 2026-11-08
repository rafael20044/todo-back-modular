package com.rafael.todo.module.subtask.model.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.rafael.todo.module.subtask.exception.SubtaskIdException;
import com.rafael.todo.module.subtask.exception.SubtaskPositionException;
import com.rafael.todo.module.subtask.exception.SubtaskTaskIdException;
import com.rafael.todo.module.subtask.exception.SubtaskTitleException;

@DisplayName("Subtask Value Objects - Unit Tests")
class SubtaskValueObjectsTest {

    @Nested
    @DisplayName("SubtaskId Value Object")
    class SubtaskIdTests {

        @Test
        @DisplayName("Should create SubtaskId from valid Long and String")
        void shouldCreateValidSubtaskId() {
            SubtaskId fromLong = SubtaskId.of(100L);
            SubtaskId fromString = SubtaskId.of("  205  ");

            assertEquals(100L, fromLong.value());
            assertEquals("100", fromLong.toString());
            assertEquals(205L, fromString.value());
        }

        @Test
        @DisplayName("Should throw SubtaskIdException when Long is null")
        void shouldThrowWhenLongIsNull() {
            SubtaskIdException ex = assertThrows(SubtaskIdException.class, () -> SubtaskId.of((Long) null));
            assertEquals("subtask.id.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -50L })
        @DisplayName("Should throw SubtaskIdException when Long is zero or negative")
        void shouldThrowWhenLongIsZeroOrNegative(long invalidId) {
            SubtaskIdException ex = assertThrows(SubtaskIdException.class, () -> SubtaskId.of(invalidId));
            assertEquals("subtask.id.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   " })
        @DisplayName("Should throw SubtaskIdException when String is null or blank")
        void shouldThrowWhenStringIsNullOrBlank(String input) {
            SubtaskIdException ex = assertThrows(SubtaskIdException.class, () -> SubtaskId.of(input));
            assertEquals("subtask.id.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @ValueSource(strings = { "abc", "0", "-10" })
        @DisplayName("Should throw SubtaskIdException when String is non-numeric or non-positive")
        void shouldThrowWhenStringIsInvalid(String invalidInput) {
            SubtaskIdException ex = assertThrows(SubtaskIdException.class, () -> SubtaskId.of(invalidInput));
            assertEquals("subtask.id.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("SubtaskTaskId Value Object")
    class SubtaskTaskIdTests {

        @Test
        @DisplayName("Should create SubtaskTaskId from valid Long and String")
        void shouldCreateValidSubtaskTaskId() {
            SubtaskTaskId fromLong = SubtaskTaskId.of(10L);
            SubtaskTaskId fromString = SubtaskTaskId.of("  25  ");

            assertEquals(10L, fromLong.value());
            assertEquals("10", fromLong.toString());
            assertEquals(25L, fromString.value());
        }

        @Test
        @DisplayName("Should throw SubtaskTaskIdException when Long is null")
        void shouldThrowWhenLongIsNull() {
            SubtaskTaskIdException ex = assertThrows(
                    SubtaskTaskIdException.class,
                    () -> SubtaskTaskId.of((Long) null)
            );
            assertEquals("subtask.task.id.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L })
        @DisplayName("Should throw SubtaskTaskIdException when Long is zero or negative")
        void shouldThrowWhenLongIsZeroOrNegative(long invalidId) {
            SubtaskTaskIdException ex = assertThrows(
                    SubtaskTaskIdException.class,
                    () -> SubtaskTaskId.of(invalidId)
            );
            assertEquals("subtask.task.id.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   " })
        @DisplayName("Should throw SubtaskTaskIdException when String is null or blank")
        void shouldThrowWhenStringIsNullOrBlank(String input) {
            SubtaskTaskIdException ex = assertThrows(
                    SubtaskTaskIdException.class,
                    () -> SubtaskTaskId.of(input)
            );
            assertEquals("subtask.task.id.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @ValueSource(strings = { "abc", "0", "-5" })
        @DisplayName("Should throw SubtaskTaskIdException when String is non-numeric or non-positive")
        void shouldThrowWhenStringIsInvalid(String invalidInput) {
            SubtaskTaskIdException ex = assertThrows(
                    SubtaskTaskIdException.class,
                    () -> SubtaskTaskId.of(invalidInput)
            );
            assertEquals("subtask.task.id.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("SubtaskTitle Value Object")
    class SubtaskTitleTests {

        @Test
        @DisplayName("Should create SubtaskTitle and trim whitespace")
        void shouldCreateValidSubtaskTitle() {
            SubtaskTitle title = SubtaskTitle.of("  Exportar gráficos de ventas  ");
            assertEquals("Exportar gráficos de ventas", title.value());
            assertEquals("Exportar gráficos de ventas", title.toString());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   ", "\n" })
        @DisplayName("Should throw SubtaskTitleException when title is null or blank")
        void shouldThrowWhenTitleIsNullOrBlank(String input) {
            SubtaskTitleException ex = assertThrows(SubtaskTitleException.class, () -> SubtaskTitle.of(input));
            assertEquals("subtask.title.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw SubtaskTitleException when title is shorter than 2 characters")
        void shouldThrowWhenTitleIsTooShort() {
            SubtaskTitleException ex = assertThrows(SubtaskTitleException.class, () -> SubtaskTitle.of("A"));
            assertEquals("subtask.title.too.short", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw SubtaskTitleException when title exceeds 200 characters")
        void shouldThrowWhenTitleIsTooLong() {
            String longTitle = "a".repeat(201);
            SubtaskTitleException ex = assertThrows(SubtaskTitleException.class, () -> SubtaskTitle.of(longTitle));
            assertEquals("subtask.title.too.long", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("SubtaskCompleted Value Object")
    class SubtaskCompletedTests {

        @Test
        @DisplayName("Should create SubtaskCompleted with completed, pending and of factories")
        void shouldCreateSubtaskCompleted() {
            SubtaskCompleted completed = SubtaskCompleted.completed();
            SubtaskCompleted pending = SubtaskCompleted.pending();
            SubtaskCompleted fromOf = SubtaskCompleted.of(true);

            assertTrue(completed.value());
            assertEquals("true", completed.toString());
            assertFalse(pending.value());
            assertEquals("false", pending.toString());
            assertTrue(fromOf.value());
        }
    }

    @Nested
    @DisplayName("SubtaskPosition Value Object")
    class SubtaskPositionTests {

        @Test
        @DisplayName("Should create SubtaskPosition from positive or zero Integer and defaultPosition")
        void shouldCreateValidSubtaskPosition() {
            SubtaskPosition pos = SubtaskPosition.of(3);
            SubtaskPosition defPos = SubtaskPosition.defaultPosition();
            SubtaskPosition fromNull = SubtaskPosition.of((Integer) null);

            assertEquals(3, pos.value());
            assertEquals("3", pos.toString());
            assertEquals(0, defPos.value());
            assertEquals(0, fromNull.value());
        }

        @ParameterizedTest
        @ValueSource(ints = { -1, -10 })
        @DisplayName("Should throw SubtaskPositionException when position is negative")
        void shouldThrowWhenPositionIsNegative(int negativePosition) {
            SubtaskPositionException ex = assertThrows(
                    SubtaskPositionException.class,
                    () -> SubtaskPosition.of(negativePosition)
            );
            assertEquals("subtask.position.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }
}

