package com.rafael.todo.module.task.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafael.todo.module.category.exception.CategoryIdException;
import com.rafael.todo.module.category.model.CategoryModel;
import com.rafael.todo.module.category.model.vo.CategoryColor;
import com.rafael.todo.module.category.model.vo.CategoryId;
import com.rafael.todo.module.category.model.vo.CategoryName;
import com.rafael.todo.module.category.model.vo.CategoryUserId;
import com.rafael.todo.module.category.service.IFindCategoryUseCase;
import com.rafael.todo.module.task.exception.TaskIdException;
import com.rafael.todo.module.task.model.TaskModel;
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
import com.rafael.todo.module.task.repository.adapter.ICreateTaskAdapter;
import com.rafael.todo.module.task.repository.adapter.IDeleteTaskAdapter;
import com.rafael.todo.module.task.repository.adapter.IFindTaskAdapter;
import com.rafael.todo.module.task.repository.adapter.IUpdateTaskAdapter;
import com.rafael.todo.module.task.service.dto.CreateTaskDtoService;
import com.rafael.todo.module.task.service.dto.UpdateTaskDtoService;
import com.rafael.todo.module.task.service.imp.TaskService;
import com.rafael.todo.module.user.exception.UserUuidException;
import com.rafael.todo.module.user.model.UserModel;
import com.rafael.todo.module.user.model.vo.UserEmail;
import com.rafael.todo.module.user.model.vo.UserName;
import com.rafael.todo.module.user.model.vo.UserPassword;
import com.rafael.todo.module.user.service.IFindUserUseCase;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService - Use Cases Unit Tests")
class TaskServiceTest {

    @Mock
    private ICreateTaskAdapter createTaskAdapter;

    @Mock
    private IUpdateTaskAdapter updateTaskAdapter;

    @Mock
    private IDeleteTaskAdapter deleteTaskAdapter;

    @Mock
    private IFindTaskAdapter findTaskAdapter;

    @Mock
    private IFindUserUseCase findUserUseCase;

    @Mock
    private IFindCategoryUseCase findCategoryUseCase;

    @InjectMocks
    private TaskService taskService;

    private UUID sampleUserId;
    private UserModel sampleUser;
    private CategoryModel sampleCategory;
    private TaskModel sampleTask;

    @BeforeEach
    void setUp() {
        sampleUser = UserModel.create(
                UserName.of("Rafael"),
                UserEmail.of("rafael@example.com"),
                UserPassword.fromRaw("Password123")
        );
        sampleUserId = sampleUser.getIdValue();

        sampleCategory = CategoryModel.reconstitute(
                CategoryId.of(1L),
                CategoryUserId.of(sampleUserId),
                CategoryName.of("Trabajo"),
                CategoryColor.defaultColor(),
                LocalDateTime.now()
        );

        sampleTask = TaskModel.reconstitute(
                TaskId.of(10L),
                TaskUserId.of(sampleUserId),
                TaskCategoryId.of(1L),
                TaskTitle.of("Preparar informe"),
                TaskDescription.of("Informe trimestral"),
                TaskStatus.pending(),
                TaskPriority.high(),
                TaskDueDate.of(LocalDateTime.of(2026, 12, 1, 18, 0)),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("Create Task Use Case")
    class CreateTaskTests {

        @Test
        @DisplayName("Should validate both userId and categoryId and create task")
        void shouldCreateTaskWithUserAndCategory() {
            CreateTaskDtoService dto = new CreateTaskDtoService(
                    sampleUserId,
                    1L,
                    "Preparar informe",
                    "Informe trimestral",
                    "HIGH",
                    LocalDateTime.of(2026, 12, 1, 18, 0)
            );

            when(findUserUseCase.findById(sampleUserId)).thenReturn(sampleUser);
            when(findCategoryUseCase.findById(1L)).thenReturn(sampleCategory);
            when(createTaskAdapter.create(any(TaskModel.class))).thenReturn(sampleTask);

            TaskModel result = taskService.create(dto);

            assertNotNull(result);
            assertEquals(10L, result.getIdValue());
            assertEquals("Preparar informe", result.getTitleValue());
            verify(findUserUseCase).findById(sampleUserId);
            verify(findCategoryUseCase).findById(1L);
            verify(createTaskAdapter).create(any(TaskModel.class));
        }

        @Test
        @DisplayName("Should skip category validation when categoryId is null")
        void shouldCreateTaskWithoutCategory() {
            CreateTaskDtoService dto = new CreateTaskDtoService(
                    sampleUserId,
                    null,
                    "Tarea sin categoría",
                    null,
                    null,
                    null
            );

            when(findUserUseCase.findById(sampleUserId)).thenReturn(sampleUser);
            when(createTaskAdapter.create(any(TaskModel.class))).thenAnswer(inv -> inv.getArgument(0));

            TaskModel result = taskService.create(dto);

            assertEquals("Tarea sin categoría", result.getTitleValue());
            assertNull(result.getCategoryIdValue());
            verify(findUserUseCase).findById(sampleUserId);
            verify(findCategoryUseCase, never()).findById(any());
        }

        @Test
        @DisplayName("Should propagate UserUuidException (404) when user does not exist")
        void shouldThrowWhenUserNotFound() {
            UUID unknownUser = UUID.randomUUID();
            CreateTaskDtoService dto = new CreateTaskDtoService(unknownUser, 1L, "Tarea", null, null, null);

            when(findUserUseCase.findById(unknownUser)).thenThrow(UserUuidException.notFound());

            UserUuidException ex = assertThrows(UserUuidException.class, () -> taskService.create(dto));
            assertEquals(404, ex.getStatusCode());
            verify(findCategoryUseCase, never()).findById(any());
            verify(createTaskAdapter, never()).create(any());
        }

        @Test
        @DisplayName("Should propagate CategoryIdException (404) when category does not exist")
        void shouldThrowWhenCategoryNotFound() {
            CreateTaskDtoService dto = new CreateTaskDtoService(sampleUserId, 99L, "Tarea", null, null, null);

            when(findUserUseCase.findById(sampleUserId)).thenReturn(sampleUser);
            when(findCategoryUseCase.findById(99L)).thenThrow(CategoryIdException.notFound());

            CategoryIdException ex = assertThrows(CategoryIdException.class, () -> taskService.create(dto));
            assertEquals(404, ex.getStatusCode());
            verify(createTaskAdapter, never()).create(any());
        }
    }

    @Nested
    @DisplayName("Find Task Use Case")
    class FindTaskTests {

        @Test
        @DisplayName("Should return TaskModel when ID exists")
        void shouldReturnTaskById() {
            when(findTaskAdapter.findById(10L)).thenReturn(Optional.of(sampleTask));

            TaskModel result = taskService.findById(10L);

            assertEquals(10L, result.getIdValue());
            assertEquals("Preparar informe", result.getTitleValue());
            verify(findTaskAdapter).findById(10L);
        }

        @Test
        @DisplayName("Should throw TaskIdException (404) when task ID does not exist")
        void shouldThrowWhenTaskNotFound() {
            when(findTaskAdapter.findById(999L)).thenReturn(Optional.empty());

            TaskIdException ex = assertThrows(TaskIdException.class, () -> taskService.findById(999L));

            assertEquals("task.id.not.found", ex.getCode());
            assertEquals(404, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should return tasks filtered by userId, categoryId, and status")
        void shouldReturnFilteredTasks() {
            when(findTaskAdapter.findAll(sampleUserId, 1L, TaskStatusEnum.PENDING))
                    .thenReturn(List.of(sampleTask));

            List<TaskModel> result = taskService.findAll(sampleUserId, 1L, TaskStatusEnum.PENDING);

            assertEquals(1, result.size());
            assertEquals(10L, result.getFirst().getIdValue());
            verify(findTaskAdapter).findAll(sampleUserId, 1L, TaskStatusEnum.PENDING);
        }
    }

    @Nested
    @DisplayName("Update Task Use Case")
    class UpdateTaskTests {

        @Test
        @DisplayName("Should validate new categoryId and update all fields including status COMPLETED")
        void shouldUpdateAllTaskFields() {
            LocalDateTime newDueDate = LocalDateTime.of(2026, 12, 15, 12, 0);
            UpdateTaskDtoService updateDto = new UpdateTaskDtoService(
                    10L,
                    2L,
                    "Informe Final",
                    "Descripción actualizada",
                    "COMPLETED",
                    "URGENT",
                    newDueDate
            );

            when(findTaskAdapter.findById(10L)).thenReturn(Optional.of(sampleTask));
            when(findCategoryUseCase.findById(2L)).thenReturn(sampleCategory);
            when(updateTaskAdapter.update(any(TaskModel.class))).thenAnswer(inv -> inv.getArgument(0));

            TaskModel updated = taskService.update(updateDto);

            assertEquals(2L, updated.getCategoryIdValue());
            assertEquals("Informe Final", updated.getTitleValue());
            assertEquals("Descripción actualizada", updated.getDescriptionValue());
            assertEquals(TaskStatusEnum.COMPLETED, updated.getStatusValue());
            assertEquals(TaskPriorityEnum.URGENT, updated.getPriorityValue());
            assertEquals(newDueDate, updated.getDueDateValue());
            assertNotNull(updated.getCompletedAt());
            verify(findCategoryUseCase).findById(2L);
            verify(updateTaskAdapter).update(any(TaskModel.class));
        }

        @Test
        @DisplayName("Should preserve existing fields when update DTO fields are null")
        void shouldPreserveExistingFieldsWhenNull() {
            UpdateTaskDtoService partialDto = new UpdateTaskDtoService(
                    10L, null, null, null, null, null, null
            );

            when(findTaskAdapter.findById(10L)).thenReturn(Optional.of(sampleTask));
            when(updateTaskAdapter.update(any(TaskModel.class))).thenAnswer(inv -> inv.getArgument(0));

            TaskModel updated = taskService.update(partialDto);

            assertEquals(sampleTask.getCategoryIdValue(), updated.getCategoryIdValue());
            assertEquals(sampleTask.getTitleValue(), updated.getTitleValue());
            assertEquals(sampleTask.getDescriptionValue(), updated.getDescriptionValue());
            assertEquals(sampleTask.getStatusValue(), updated.getStatusValue());
            assertEquals(sampleTask.getPriorityValue(), updated.getPriorityValue());
            assertEquals(sampleTask.getDueDateValue(), updated.getDueDateValue());
            verify(findCategoryUseCase, never()).findById(any());
        }

        @Test
        @DisplayName("Should throw TaskIdException (404) when updating non-existent task")
        void shouldThrowWhenUpdatingNonExistentTask() {
            UpdateTaskDtoService dto = new UpdateTaskDtoService(999L, null, "Título", null, null, null, null);
            when(findTaskAdapter.findById(999L)).thenReturn(Optional.empty());

            TaskIdException ex = assertThrows(TaskIdException.class, () -> taskService.update(dto));
            assertEquals(404, ex.getStatusCode());
            verify(updateTaskAdapter, never()).update(any());
        }
    }

    @Nested
    @DisplayName("Delete Task Use Case")
    class DeleteTaskTests {

        @Test
        @DisplayName("Should delegate task deletion by ID to deleteTaskAdapter")
        void shouldDeleteTaskById() {
            taskService.delete(10L);

            verify(deleteTaskAdapter).delete(10L);
        }
    }
}

