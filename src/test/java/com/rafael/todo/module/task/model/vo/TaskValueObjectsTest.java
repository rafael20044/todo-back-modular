package com.rafael.todo.module.task.model.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.rafael.todo.module.task.exception.TaskCategoryIdException;
import com.rafael.todo.module.task.exception.TaskDescriptionException;
import com.rafael.todo.module.task.exception.TaskIdException;
import com.rafael.todo.module.task.exception.TaskPriorityException;
import com.rafael.todo.module.task.exception.TaskStatusException;
import com.rafael.todo.module.task.exception.TaskTitleException;
import com.rafael.todo.module.task.exception.TaskUserIdException;
import com.rafael.todo.module.task.model.enums.TaskPriorityEnum;
import com.rafael.todo.module.task.model.enums.TaskStatusEnum;

@DisplayName("Task Value Objects - Unit Tests")
class TaskValueObjectsTest {

    @Nested
    @DisplayName("TaskId Value Object")
    class TaskIdTests {

        @Test
        @DisplayName("Should create TaskId from valid Long and String")
        void shouldCreateValidTaskId() {
            TaskId fromLong = TaskId.of(15L);
            TaskId fromString = TaskId.of("  42  ");

            assertEquals(15L, fromLong.value());
            assertEquals("15", fromLong.toString());
            assertEquals(42L, fromString.value());
        }

        @Test
        @DisplayName("Should throw TaskIdException when Long is null")
        void shouldThrowWhenLongIsNull() {
            TaskIdException ex = assertThrows(TaskIdException.class, () -> TaskId.of((Long) null));
            assertEquals("task.id.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @DisplayName("Should throw TaskIdException when Long is zero or negative")
        void shouldThrowWhenLongIsZeroOrNegative(long invalidId) {
            TaskIdException ex = assertThrows(TaskIdException.class, () -> TaskId.of(invalidId));
            assertEquals("task.id.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   " })
        @DisplayName("Should throw TaskIdException when String is null or blank")
        void shouldThrowWhenStringIsNullOrBlank(String input) {
            TaskIdException ex = assertThrows(TaskIdException.class, () -> TaskId.of(input));
            assertEquals("task.id.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @ValueSource(strings = { "abc", "0", "-5" })
        @DisplayName("Should throw TaskIdException when String is non-numeric or non-positive")
        void shouldThrowWhenStringIsInvalid(String invalidInput) {
            TaskIdException ex = assertThrows(TaskIdException.class, () -> TaskId.of(invalidInput));
            assertEquals("task.id.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("TaskUserId Value Object")
    class TaskUserIdTests {

        @Test
        @DisplayName("Should create TaskUserId from valid UUID and String")
        void shouldCreateValidTaskUserId() {
            UUID rawUuid = UUID.randomUUID();
            TaskUserId fromUuid = TaskUserId.of(rawUuid);
            TaskUserId fromString = TaskUserId.of("  " + rawUuid + "  ");

            assertEquals(rawUuid, fromUuid.value());
            assertEquals(rawUuid.toString(), fromUuid.toString());
            assertEquals(rawUuid, fromString.value());
        }

        @Test
        @DisplayName("Should throw TaskUserIdException when UUID is null")
        void shouldThrowWhenUuidIsNull() {
            TaskUserIdException ex = assertThrows(TaskUserIdException.class, () -> TaskUserId.of((UUID) null));
            assertEquals("task.user.id.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   " })
        @DisplayName("Should throw TaskUserIdException when String is null or blank")
        void shouldThrowWhenStringIsNullOrBlank(String input) {
            TaskUserIdException ex = assertThrows(TaskUserIdException.class, () -> TaskUserId.of(input));
            assertEquals("task.user.id.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw TaskUserIdException when String has invalid UUID format")
        void shouldThrowWhenStringFormatIsInvalid() {
            TaskUserIdException ex = assertThrows(TaskUserIdException.class, () -> TaskUserId.of("invalid-uuid"));
            assertEquals("task.user.id.invalid.format", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("TaskCategoryId Value Object")
    class TaskCategoryIdTests {

        @Test
        @DisplayName("Should create TaskCategoryId from valid Long and String")
        void shouldCreateValidTaskCategoryId() {
            TaskCategoryId fromLong = TaskCategoryId.of(3L);
            TaskCategoryId fromString = TaskCategoryId.of("  8  ");

            assertEquals(3L, fromLong.value());
            assertEquals("3", fromLong.toString());
            assertEquals(8L, fromString.value());
        }

        @Test
        @DisplayName("Should throw TaskCategoryIdException when Long is null")
        void shouldThrowWhenLongIsNull() {
            TaskCategoryIdException ex = assertThrows(
                    TaskCategoryIdException.class,
                    () -> TaskCategoryId.of((Long) null)
            );
            assertEquals("task.category.id.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L })
        @DisplayName("Should throw TaskCategoryIdException when Long is zero or negative")
        void shouldThrowWhenLongIsZeroOrNegative(long invalidId) {
            TaskCategoryIdException ex = assertThrows(
                    TaskCategoryIdException.class,
                    () -> TaskCategoryId.of(invalidId)
            );
            assertEquals("task.category.id.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   ", "abc", "0", "-3" })
        @DisplayName("Should throw TaskCategoryIdException when String is null, blank, or invalid")
        void shouldThrowWhenStringIsInvalid(String invalidInput) {
            TaskCategoryIdException ex = assertThrows(
                    TaskCategoryIdException.class,
                    () -> TaskCategoryId.of(invalidInput)
            );
            assertEquals("task.category.id.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("TaskTitle Value Object")
    class TaskTitleTests {

        @Test
        @DisplayName("Should create TaskTitle and trim whitespace")
        void shouldCreateValidTaskTitle() {
            TaskTitle title = TaskTitle.of("  Preparar informe  ");
            assertEquals("Preparar informe", title.value());
            assertEquals("Preparar informe", title.toString());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   ", "\t" })
        @DisplayName("Should throw TaskTitleException when title is null or blank")
        void shouldThrowWhenTitleIsNullOrBlank(String input) {
            TaskTitleException ex = assertThrows(TaskTitleException.class, () -> TaskTitle.of(input));
            assertEquals("task.title.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw TaskTitleException when title is shorter than 2 characters")
        void shouldThrowWhenTitleIsTooShort() {
            TaskTitleException ex = assertThrows(TaskTitleException.class, () -> TaskTitle.of("A"));
            assertEquals("task.title.too.short", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw TaskTitleException when title exceeds 150 characters")
        void shouldThrowWhenTitleIsTooLong() {
            String longTitle = "a".repeat(151);
            TaskTitleException ex = assertThrows(TaskTitleException.class, () -> TaskTitle.of(longTitle));
            assertEquals("task.title.too.long", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("TaskDescription Value Object")
    class TaskDescriptionTests {

        @Test
        @DisplayName("Should create TaskDescription and trim whitespace")
        void shouldCreateValidTaskDescription() {
            TaskDescription desc = TaskDescription.of("  Detalle de la tarea  ");
            assertEquals("Detalle de la tarea", desc.value());
            assertEquals("Detalle de la tarea", desc.toString());
        }

        @Test
        @DisplayName("Should normalize blank or null description to null and empty toString")
        void shouldNormalizeBlankAndNullToNull() {
            TaskDescription emptyVo = TaskDescription.empty();
            TaskDescription fromBlank = TaskDescription.of("   ");

            assertNull(emptyVo.value());
            assertEquals("", emptyVo.toString());
            assertNull(fromBlank.value());
        }

        @Test
        @DisplayName("Should throw TaskDescriptionException when description exceeds 2000 characters")
        void shouldThrowWhenDescriptionIsTooLong() {
            String longDesc = "a".repeat(2001);
            TaskDescriptionException ex = assertThrows(
                    TaskDescriptionException.class,
                    () -> TaskDescription.of(longDesc)
            );
            assertEquals("task.description.too.long", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("TaskStatus Value Object")
    class TaskStatusTests {

        @Test
        @DisplayName("Should create TaskStatus via factory methods and String")
        void shouldCreateValidTaskStatuses() {
            assertEquals(TaskStatusEnum.PENDING, TaskStatus.pending().value());
            assertEquals(TaskStatusEnum.IN_PROGRESS, TaskStatus.inProgress().value());
            assertEquals(TaskStatusEnum.COMPLETED, TaskStatus.completed().value());
            assertEquals(TaskStatusEnum.CANCELLED, TaskStatus.cancelled().value());

            TaskStatus fromString = TaskStatus.of("  in_progress  ");
            assertEquals(TaskStatusEnum.IN_PROGRESS, fromString.value());
            assertEquals("IN_PROGRESS", fromString.toString());
        }

        @Test
        @DisplayName("Should throw TaskStatusException when enum is null")
        void shouldThrowWhenEnumIsNull() {
            TaskStatusException ex = assertThrows(TaskStatusException.class, () -> TaskStatus.of((TaskStatusEnum) null));
            assertEquals("task.status.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   " })
        @DisplayName("Should throw TaskStatusException when status String is null or blank")
        void shouldThrowWhenStatusStringIsNullOrBlank(String input) {
            TaskStatusException ex = assertThrows(TaskStatusException.class, () -> TaskStatus.of(input));
            assertEquals("task.status.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw TaskStatusException when status String is invalid")
        void shouldThrowWhenStatusStringIsInvalid() {
            TaskStatusException ex = assertThrows(TaskStatusException.class, () -> TaskStatus.of("ARCHIVED"));
            assertEquals("task.status.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("TaskPriority Value Object")
    class TaskPriorityTests {

        @Test
        @DisplayName("Should create TaskPriority via factory methods and String")
        void shouldCreateValidTaskPriorities() {
            assertEquals(TaskPriorityEnum.LOW, TaskPriority.low().value());
            assertEquals(TaskPriorityEnum.MEDIUM, TaskPriority.medium().value());
            assertEquals(TaskPriorityEnum.HIGH, TaskPriority.high().value());
            assertEquals(TaskPriorityEnum.URGENT, TaskPriority.urgent().value());

            TaskPriority fromString = TaskPriority.of("  urgent  ");
            assertEquals(TaskPriorityEnum.URGENT, fromString.value());
            assertEquals("URGENT", fromString.toString());
        }

        @Test
        @DisplayName("Should throw TaskPriorityException when enum is null")
        void shouldThrowWhenPriorityEnumIsNull() {
            TaskPriorityException ex = assertThrows(
                    TaskPriorityException.class,
                    () -> TaskPriority.of((TaskPriorityEnum) null)
            );
            assertEquals("task.priority.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   " })
        @DisplayName("Should throw TaskPriorityException when priority String is null or blank")
        void shouldThrowWhenPriorityStringIsNullOrBlank(String input) {
            TaskPriorityException ex = assertThrows(TaskPriorityException.class, () -> TaskPriority.of(input));
            assertEquals("task.priority.required", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should throw TaskPriorityException when priority String is invalid")
        void shouldThrowWhenPriorityStringIsInvalid() {
            TaskPriorityException ex = assertThrows(TaskPriorityException.class, () -> TaskPriority.of("CRITICAL"));
            assertEquals("task.priority.invalid", ex.getCode());
            assertEquals(400, ex.getStatusCode());
        }
    }

    @Nested
    @DisplayName("TaskDueDate Value Object")
    class TaskDueDateTests {

        @Test
        @DisplayName("Should create TaskDueDate from LocalDateTime and ISO String")
        void shouldCreateValidTaskDueDate() {
            LocalDateTime dt = LocalDateTime.of(2026, 12, 31, 18, 0);
            TaskDueDate fromDt = TaskDueDate.of(dt);
            TaskDueDate fromIso = TaskDueDate.of("  2026-12-31T18:00:00  ");

            assertEquals(dt, fromDt.value());
            assertEquals(dt, fromIso.value());
            assertEquals(dt.toString(), fromDt.toString());
        }

        @Test
        @DisplayName("Should create empty TaskDueDate when null or blank")
        void shouldCreateEmptyTaskDueDate() {
            TaskDueDate emptyVo = TaskDueDate.empty();
            TaskDueDate fromBlank = TaskDueDate.of("   ");

            assertNull(emptyVo.value());
            assertEquals("", emptyVo.toString());
            assertNull(fromBlank.value());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException on malformed ISO String")
        void shouldThrowOnMalformedIsoString() {
            assertThrows(IllegalArgumentException.class, () -> TaskDueDate.of("not-a-date"));
        }
    }
}

