package com.rafael.todo.module.task.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rafael.todo.module.task.model.enums.TaskPriorityEnum;
import com.rafael.todo.module.task.model.enums.TaskStatusEnum;
import com.rafael.todo.module.task.model.vo.TaskCategoryId;
import com.rafael.todo.module.task.model.vo.TaskDescription;
import com.rafael.todo.module.task.model.vo.TaskDueDate;
import com.rafael.todo.module.task.model.vo.TaskId;
import com.rafael.todo.module.task.model.vo.TaskPriority;
import com.rafael.todo.module.task.model.vo.TaskStatus;
import com.rafael.todo.module.task.model.vo.TaskTitle;
import com.rafael.todo.module.task.model.vo.TaskUserId;

@DisplayName("TaskModel - Domain Model Unit Tests")
class TaskModelTest {

    @Test
    @DisplayName("Should create TaskModel with minimal arguments and default PENDING/MEDIUM state")
    void shouldCreateMinimalTaskModel() {
        UUID userId = UUID.randomUUID();
        TaskModel task = TaskModel.create(TaskUserId.of(userId), TaskTitle.of("Comprar víveres"));

        assertNull(task.getIdValue());
        assertEquals(userId, task.getUserIdValue());
        assertNull(task.getCategoryIdValue());
        assertEquals("Comprar víveres", task.getTitleValue());
        assertNull(task.getDescriptionValue());
        assertEquals(TaskStatusEnum.PENDING, task.getStatusValue());
        assertEquals(TaskPriorityEnum.MEDIUM, task.getPriorityValue());
        assertNull(task.getDueDateValue());
        assertNull(task.getCompletedAt());
        assertNotNull(task.getCreatedAt());
        assertNotNull(task.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create TaskModel with all optional fields")
    void shouldCreateFullTaskModel() {
        UUID userId = UUID.randomUUID();
        LocalDateTime dueDate = LocalDateTime.of(2026, 11, 20, 17, 0);

        TaskModel task = TaskModel.create(
                TaskUserId.of(userId),
                TaskCategoryId.of(3L),
                TaskTitle.of("Entregar reporte"),
                TaskDescription.of("Reporte mensual de métricas"),
                TaskPriority.high(),
                TaskDueDate.of(dueDate)
        );

        assertEquals(3L, task.getCategoryIdValue());
        assertEquals("Entregar reporte", task.getTitleValue());
        assertEquals("Reporte mensual de métricas", task.getDescriptionValue());
        assertEquals(TaskStatusEnum.PENDING, task.getStatusValue());
        assertEquals(TaskPriorityEnum.HIGH, task.getPriorityValue());
        assertEquals(dueDate, task.getDueDateValue());
    }

    @Test
    @DisplayName("Should reconstitute TaskModel preserving all persisted attributes")
    void shouldReconstituteTaskModel() {
        UUID userId = UUID.randomUUID();
        LocalDateTime dueDate = LocalDateTime.of(2026, 11, 25, 10, 0);
        LocalDateTime completedAt = LocalDateTime.of(2026, 11, 24, 15, 0);
        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 1, 8, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 11, 24, 15, 0);

        TaskModel task = TaskModel.reconstitute(
                TaskId.of(10L),
                TaskUserId.of(userId),
                TaskCategoryId.of(2L),
                TaskTitle.of("Tarea completada"),
                TaskDescription.of("Descripción"),
                TaskStatus.completed(),
                TaskPriority.urgent(),
                TaskDueDate.of(dueDate),
                completedAt,
                createdAt,
                updatedAt
        );

        assertEquals(10L, task.getIdValue());
        assertEquals(userId, task.getUserIdValue());
        assertEquals(2L, task.getCategoryIdValue());
        assertEquals("Tarea completada", task.getTitleValue());
        assertEquals("Descripción", task.getDescriptionValue());
        assertEquals(TaskStatusEnum.COMPLETED, task.getStatusValue());
        assertEquals(TaskPriorityEnum.URGENT, task.getPriorityValue());
        assertEquals(dueDate, task.getDueDateValue());
        assertEquals(completedAt, task.getCompletedAt());
        assertEquals(createdAt, task.getCreatedAt());
        assertEquals(updatedAt, task.getUpdatedAt());
    }

    @Test
    @DisplayName("Should perform copy-on-write mutations and manage completedAt automatically")
    void shouldPerformMutationsAndManageCompletedAt() {
        UUID userId = UUID.randomUUID();
        TaskModel original = TaskModel.create(TaskUserId.of(userId), TaskTitle.of("Original"));

        TaskModel updatedFields = original
                .changeTitle(TaskTitle.of("Nuevo Título"))
                .changeDescription(TaskDescription.of("Nueva Descripción"))
                .changeCategory(TaskCategoryId.of(5L))
                .changePriority(TaskPriority.urgent())
                .changeDueDate(TaskDueDate.of(LocalDateTime.of(2026, 12, 1, 12, 0)));

        assertNotSame(original, updatedFields);
        assertEquals("Original", original.getTitleValue());
        assertEquals("Nuevo Título", updatedFields.getTitleValue());
        assertEquals("Nueva Descripción", updatedFields.getDescriptionValue());
        assertEquals(5L, updatedFields.getCategoryIdValue());
        assertEquals(TaskPriorityEnum.URGENT, updatedFields.getPriorityValue());
        assertNull(updatedFields.getCompletedAt());

        // markCompleted sets status to COMPLETED and assigns completedAt
        TaskModel completed = updatedFields.markCompleted();
        assertEquals(TaskStatusEnum.COMPLETED, completed.getStatusValue());
        assertNotNull(completed.getCompletedAt());

        // markPending resets status to PENDING and clears completedAt
        TaskModel pendingAgain = completed.markPending();
        assertEquals(TaskStatusEnum.PENDING, pendingAgain.getStatusValue());
        assertNull(pendingAgain.getCompletedAt());

        // changeStatus(COMPLETED) assigns completedAt, and changing to IN_PROGRESS clears it
        TaskModel completedViaStatus = pendingAgain.changeStatus(TaskStatus.completed());
        assertEquals(TaskStatusEnum.COMPLETED, completedViaStatus.getStatusValue());
        assertNotNull(completedViaStatus.getCompletedAt());

        TaskModel inProgress = completedViaStatus.changeStatus(TaskStatus.inProgress());
        assertEquals(TaskStatusEnum.IN_PROGRESS, inProgress.getStatusValue());
        assertNull(inProgress.getCompletedAt());
    }
}

