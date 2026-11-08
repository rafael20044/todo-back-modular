package com.rafael.todo.module.task.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafael.todo.module.task.model.TaskModel;
import com.rafael.todo.module.task.model.enums.TaskStatusEnum;
import com.rafael.todo.module.task.model.vo.TaskCategoryId;
import com.rafael.todo.module.task.model.vo.TaskDescription;
import com.rafael.todo.module.task.model.vo.TaskDueDate;
import com.rafael.todo.module.task.model.vo.TaskId;
import com.rafael.todo.module.task.model.vo.TaskPriority;
import com.rafael.todo.module.task.model.vo.TaskStatus;
import com.rafael.todo.module.task.model.vo.TaskTitle;
import com.rafael.todo.module.task.model.vo.TaskUserId;
import com.rafael.todo.module.task.repository.adapter.imp.TaskAdapter;
import com.rafael.todo.module.task.repository.entity.TaskEntity;
import com.rafael.todo.module.task.repository.mapper.TaskMapperRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskAdapter - Persistence Adapter Unit Tests")
class TaskAdapterTest {

    @Mock
    private ITaskRepository taskRepository;

    @InjectMocks
    private TaskAdapter taskAdapter;

    private UUID sampleUserId;
    private TaskModel sampleModel;
    private TaskEntity sampleEntity;

    @BeforeEach
    void setUp() {
        sampleUserId = UUID.randomUUID();
        sampleModel = TaskModel.reconstitute(
                TaskId.of(10L),
                TaskUserId.of(sampleUserId),
                TaskCategoryId.of(1L),
                TaskTitle.of("Tarea 1"),
                TaskDescription.of("Descripción"),
                TaskStatus.pending(),
                TaskPriority.medium(),
                TaskDueDate.empty(),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        sampleEntity = TaskMapperRepository.toEntity(sampleModel);
    }

    @Test
    @DisplayName("Should persist new task entity and return reconstituted TaskModel in create")
    void shouldCreateTask() {
        TaskModel newModel = TaskModel.create(TaskUserId.of(sampleUserId), TaskTitle.of("Tarea 1"));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(sampleEntity);

        TaskModel created = taskAdapter.create(newModel);

        assertEquals(10L, created.getIdValue());
        assertEquals("Tarea 1", created.getTitleValue());
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Should update task entity and return reconstituted TaskModel")
    void shouldUpdateTask() {
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(sampleEntity);

        TaskModel updated = taskAdapter.update(sampleModel);

        assertEquals(10L, updated.getIdValue());
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Should find task by ID and map to TaskModel")
    void shouldFindById() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(sampleEntity));

        Optional<TaskModel> found = taskAdapter.findById(10L);

        assertTrue(found.isPresent());
        assertEquals(10L, found.get().getIdValue());
    }

    @Test
    @DisplayName("Should return empty Optional when task ID does not exist")
    void shouldReturnEmptyWhenIdNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<TaskModel> found = taskAdapter.findById(99L);

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find all tasks with non-null status filter")
    void shouldFindAllWithStatusFilter() {
        when(taskRepository.findAllFiltered(sampleUserId, 1L, "PENDING"))
                .thenReturn(List.of(sampleEntity));

        List<TaskModel> tasks = taskAdapter.findAll(sampleUserId, 1L, TaskStatusEnum.PENDING);

        assertEquals(1, tasks.size());
        assertEquals(10L, tasks.getFirst().getIdValue());
        verify(taskRepository).findAllFiltered(sampleUserId, 1L, "PENDING");
    }

    @Test
    @DisplayName("Should pass null status string when status enum filter is null")
    void shouldFindAllWithNullStatusFilter() {
        when(taskRepository.findAllFiltered(sampleUserId, null, null))
                .thenReturn(List.of(sampleEntity));

        List<TaskModel> tasks = taskAdapter.findAll(sampleUserId, null, null);

        assertEquals(1, tasks.size());
        verify(taskRepository).findAllFiltered(sampleUserId, null, null);
    }

    @Test
    @DisplayName("Should delete task by ID in repository")
    void shouldDeleteById() {
        taskAdapter.delete(10L);

        verify(taskRepository).deleteById(10L);
    }
}

