package com.rafael.todo.module.subtask.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.rafael.todo.module.subtask.exception.SubtaskIdException;
import com.rafael.todo.module.subtask.model.SubtaskModel;
import com.rafael.todo.module.subtask.model.vo.SubtaskCompleted;
import com.rafael.todo.module.subtask.model.vo.SubtaskId;
import com.rafael.todo.module.subtask.model.vo.SubtaskPosition;
import com.rafael.todo.module.subtask.model.vo.SubtaskTaskId;
import com.rafael.todo.module.subtask.model.vo.SubtaskTitle;
import com.rafael.todo.module.subtask.repository.adapter.ICreateSubtaskAdapter;
import com.rafael.todo.module.subtask.repository.adapter.IDeleteSubtaskAdapter;
import com.rafael.todo.module.subtask.repository.adapter.IFindSubtaskAdapter;
import com.rafael.todo.module.subtask.repository.adapter.IUpdateSubtaskAdapter;
import com.rafael.todo.module.subtask.service.dto.CreateSubtaskDtoService;
import com.rafael.todo.module.subtask.service.dto.UpdateSubtaskDtoService;
import com.rafael.todo.module.subtask.service.imp.SubtaskService;
import com.rafael.todo.module.task.exception.TaskIdException;
import com.rafael.todo.module.task.model.TaskModel;
import com.rafael.todo.module.task.model.vo.TaskId;
import com.rafael.todo.module.task.model.vo.TaskPriority;
import com.rafael.todo.module.task.model.vo.TaskStatus;
import com.rafael.todo.module.task.model.vo.TaskTitle;
import com.rafael.todo.module.task.model.vo.TaskUserId;
import com.rafael.todo.module.task.service.IFindTaskUseCase;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubtaskService - Use Cases Unit Tests")
class SubtaskServiceTest {

    @Mock
    private ICreateSubtaskAdapter createSubtaskAdapter;

    @Mock
    private IUpdateSubtaskAdapter updateSubtaskAdapter;

    @Mock
    private IDeleteSubtaskAdapter deleteSubtaskAdapter;

    @Mock
    private IFindSubtaskAdapter findSubtaskAdapter;

    @Mock
    private IFindTaskUseCase findTaskUseCase;

    @InjectMocks
    private SubtaskService subtaskService;

    private TaskModel sampleTask;
    private SubtaskModel sampleSubtask;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        sampleTask = TaskModel.reconstitute(
                TaskId.of(10L),
                TaskUserId.of(UUID.randomUUID()),
                null,
                TaskTitle.of("Tarea Principal"),
                null,
                TaskStatus.pending(),
                TaskPriority.medium(),
                null,
                null,
                now,
                now
        );

        sampleSubtask = SubtaskModel.reconstitute(
                SubtaskId.of(100L),
                SubtaskTaskId.of(10L),
                SubtaskTitle.of("Exportar gráficos"),
                SubtaskCompleted.pending(),
                SubtaskPosition.of(1),
                now,
                now
        );
    }

    @Nested
    @DisplayName("Create Subtask Use Case")
    class CreateSubtaskTests {

        @Test
        @DisplayName("Should validate task existence via IFindTaskUseCase and create subtask")
        void shouldCreateSubtaskWhenTaskExists() {
            CreateSubtaskDtoService dto = new CreateSubtaskDtoService(10L, "Exportar gráficos", 1);

            when(findTaskUseCase.findById(10L)).thenReturn(sampleTask);
            when(createSubtaskAdapter.create(any(SubtaskModel.class))).thenReturn(sampleSubtask);

            SubtaskModel result = subtaskService.create(dto);

            assertNotNull(result);
            assertEquals(100L, result.getIdValue());
            assertEquals(10L, result.getTaskIdValue());
            assertEquals("Exportar gráficos", result.getTitleValue());
            verify(findTaskUseCase).findById(10L);
            verify(createSubtaskAdapter).create(any(SubtaskModel.class));
        }

        @Test
        @DisplayName("Should propagate TaskIdException (404) when parent task does not exist")
        void shouldThrowWhenParentTaskDoesNotExist() {
            CreateSubtaskDtoService dto = new CreateSubtaskDtoService(999L, "Subtarea", 1);

            when(findTaskUseCase.findById(999L)).thenThrow(TaskIdException.notFound());

            TaskIdException ex = assertThrows(TaskIdException.class, () -> subtaskService.create(dto));

            assertEquals("task.id.not.found", ex.getCode());
            assertEquals(404, ex.getStatusCode());
            verify(createSubtaskAdapter, never()).create(any());
        }
    }

    @Nested
    @DisplayName("Find Subtask Use Case")
    class FindSubtaskTests {

        @Test
        @DisplayName("Should return SubtaskModel when ID exists")
        void shouldReturnSubtaskById() {
            when(findSubtaskAdapter.findById(100L)).thenReturn(Optional.of(sampleSubtask));

            SubtaskModel result = subtaskService.findById(100L);

            assertEquals(100L, result.getIdValue());
            assertEquals("Exportar gráficos", result.getTitleValue());
            verify(findSubtaskAdapter).findById(100L);
        }

        @Test
        @DisplayName("Should throw SubtaskIdException (404) when subtask ID does not exist")
        void shouldThrowWhenSubtaskNotFound() {
            when(findSubtaskAdapter.findById(999L)).thenReturn(Optional.empty());

            SubtaskIdException ex = assertThrows(SubtaskIdException.class, () -> subtaskService.findById(999L));

            assertEquals("subtask.id.not.found", ex.getCode());
            assertEquals(404, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should validate taskId and return subtasks ordered by position")
        void shouldReturnAllByTaskId() {
            when(findTaskUseCase.findById(10L)).thenReturn(sampleTask);
            when(findSubtaskAdapter.findAllByTaskId(10L)).thenReturn(List.of(sampleSubtask));

            List<SubtaskModel> result = subtaskService.findAllByTaskId(10L);

            assertEquals(1, result.size());
            assertEquals(100L, result.getFirst().getIdValue());
            verify(findTaskUseCase).findById(10L);
            verify(findSubtaskAdapter).findAllByTaskId(10L);
        }

        @Test
        @DisplayName("Should propagate TaskIdException (404) in findAllByTaskId when task does not exist")
        void shouldThrowInFindAllWhenTaskDoesNotExist() {
            when(findTaskUseCase.findById(999L)).thenThrow(TaskIdException.notFound());

            TaskIdException ex = assertThrows(TaskIdException.class, () -> subtaskService.findAllByTaskId(999L));
            assertEquals(404, ex.getStatusCode());
            verify(findSubtaskAdapter, never()).findAllByTaskId(any());
        }
    }

    @Nested
    @DisplayName("Update Subtask Use Case")
    class UpdateSubtaskTests {

        @Test
        @DisplayName("Should update title, mark completed, and update position")
        void shouldUpdateAllSubtaskFields() {
            UpdateSubtaskDtoService updateDto = new UpdateSubtaskDtoService(
                    100L,
                    "Gráficos validados",
                    true,
                    3
            );

            when(findSubtaskAdapter.findById(100L)).thenReturn(Optional.of(sampleSubtask));
            when(updateSubtaskAdapter.update(any(SubtaskModel.class))).thenAnswer(inv -> inv.getArgument(0));

            SubtaskModel updated = subtaskService.update(updateDto);

            assertEquals("Gráficos validados", updated.getTitleValue());
            assertTrue(updated.isCompletedValue());
            assertEquals(3, updated.getPositionValue());
            verify(updateSubtaskAdapter).update(any(SubtaskModel.class));
        }

        @Test
        @DisplayName("Should mark pending when isCompleted is false and preserve null fields")
        void shouldMarkPendingAndPreserveNullFields() {
            SubtaskModel completedSubtask = sampleSubtask.markCompleted();
            UpdateSubtaskDtoService partialDto = new UpdateSubtaskDtoService(100L, null, false, null);

            when(findSubtaskAdapter.findById(100L)).thenReturn(Optional.of(completedSubtask));
            when(updateSubtaskAdapter.update(any(SubtaskModel.class))).thenAnswer(inv -> inv.getArgument(0));

            SubtaskModel updated = subtaskService.update(partialDto);

            assertEquals(completedSubtask.getTitleValue(), updated.getTitleValue());
            assertFalse(updated.isCompletedValue());
            assertEquals(completedSubtask.getPositionValue(), updated.getPositionValue());
        }

        @Test
        @DisplayName("Should throw SubtaskIdException (404) when updating non-existent subtask")
        void shouldThrowWhenUpdatingNonExistentSubtask() {
            UpdateSubtaskDtoService updateDto = new UpdateSubtaskDtoService(999L, "Nuevo", true, 1);
            when(findSubtaskAdapter.findById(999L)).thenReturn(Optional.empty());

            SubtaskIdException ex = assertThrows(SubtaskIdException.class, () -> subtaskService.update(updateDto));
            assertEquals(404, ex.getStatusCode());
            verify(updateSubtaskAdapter, never()).update(any());
        }
    }

    @Nested
    @DisplayName("Delete Subtask Use Case")
    class DeleteSubtaskTests {

        @Test
        @DisplayName("Should delegate subtask deletion by ID to deleteSubtaskAdapter")
        void shouldDeleteSubtaskById() {
            subtaskService.delete(100L);

            verify(deleteSubtaskAdapter).delete(100L);
        }
    }
}

