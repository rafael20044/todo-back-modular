package com.rafael.todo.module.task.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import com.rafael.todo.module.task.controller.dto.CreateTaskDto;
import com.rafael.todo.module.task.controller.dto.ResponseTaskDto;
import com.rafael.todo.module.task.controller.dto.UpdateTaskDto;
import com.rafael.todo.module.task.controller.mapper.TaskMapperController;
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
import com.rafael.todo.module.task.repository.entity.TaskEntity;
import com.rafael.todo.module.task.repository.mapper.TaskMapperRepository;
import com.rafael.todo.module.task.service.dto.CreateTaskDtoService;
import com.rafael.todo.module.task.service.dto.UpdateTaskDtoService;
import com.rafael.todo.module.task.service.mapper.TaskMapperService;

@DisplayName("Task Mappers (Controller, Service, Repository) - Unit Tests")
class TaskMappersTest {

    @Test
    @DisplayName("TaskMapperController should map DTOs, lists and pages accurately")
    void shouldMapInTaskMapperController() {
        UUID userId = UUID.randomUUID();
        LocalDateTime dueDate = LocalDateTime.of(2026, 11, 30, 18, 0);

        CreateTaskDto createDto = new CreateTaskDto(userId, 2L, "Tarea 1", "Desc 1", "HIGH", dueDate);
        CreateTaskDtoService serviceCreateDto = TaskMapperController.toServiceDto(createDto);

        assertEquals(userId, serviceCreateDto.userId());
        assertEquals(2L, serviceCreateDto.categoryId());
        assertEquals("Tarea 1", serviceCreateDto.title());
        assertEquals("Desc 1", serviceCreateDto.description());
        assertEquals("HIGH", serviceCreateDto.priority());
        assertEquals(dueDate, serviceCreateDto.dueDate());

        UpdateTaskDto updateDto = new UpdateTaskDto(3L, "Tarea Editada", "Desc Editada", "COMPLETED", "URGENT", dueDate);
        UpdateTaskDtoService serviceUpdateDto = TaskMapperController.toServiceDto(10L, updateDto);

        assertEquals(10L, serviceUpdateDto.id());
        assertEquals(3L, serviceUpdateDto.categoryId());
        assertEquals("Tarea Editada", serviceUpdateDto.title());
        assertEquals("Desc Editada", serviceUpdateDto.description());
        assertEquals("COMPLETED", serviceUpdateDto.status());
        assertEquals("URGENT", serviceUpdateDto.priority());
        assertEquals(dueDate, serviceUpdateDto.dueDate());

        LocalDateTime now = LocalDateTime.now();
        TaskModel model = TaskModel.reconstitute(
                TaskId.of(10L),
                TaskUserId.of(userId),
                TaskCategoryId.of(3L),
                TaskTitle.of("Tarea Editada"),
                TaskDescription.of("Desc Editada"),
                TaskStatus.completed(),
                TaskPriority.urgent(),
                TaskDueDate.of(dueDate),
                now,
                now,
                now
        );

        ResponseTaskDto responseDto = TaskMapperController.toResponseDto(model);
        assertEquals(10L, responseDto.id());
        assertEquals(userId, responseDto.userId());
        assertEquals(3L, responseDto.categoryId());
        assertEquals("Tarea Editada", responseDto.title());
        assertEquals("Desc Editada", responseDto.description());
        assertEquals("COMPLETED", responseDto.status());
        assertEquals("URGENT", responseDto.priority());
        assertEquals(dueDate, responseDto.dueDate());
        assertEquals(now, responseDto.completedAt());

        List<ResponseTaskDto> list = TaskMapperController.toResponseList(List.of(model));
        assertEquals(1, list.size());

        Page<ResponseTaskDto> page = TaskMapperController.toPage(List.of(model), 0, 20);
        assertEquals(1, page.getTotalElements());
        assertEquals("Tarea Editada", page.getContent().getFirst().title());
    }

    @Test
    @DisplayName("TaskMapperService should map CreateTaskDtoService with optional fields null or present")
    void shouldMapInTaskMapperService() {
        UUID userId = UUID.randomUUID();
        LocalDateTime dueDate = LocalDateTime.of(2026, 12, 10, 9, 0);

        CreateTaskDtoService withNullOptionals = new CreateTaskDtoService(userId, null, "Simple Task", null, null, null);
        TaskModel minimalModel = TaskMapperService.toTaskModel(withNullOptionals);

        assertEquals(userId, minimalModel.getUserIdValue());
        assertNull(minimalModel.getCategoryIdValue());
        assertEquals("Simple Task", minimalModel.getTitleValue());
        assertNull(minimalModel.getDescriptionValue());
        assertEquals(TaskStatusEnum.PENDING, minimalModel.getStatusValue());
        assertEquals(TaskPriorityEnum.MEDIUM, minimalModel.getPriorityValue());
        assertNull(minimalModel.getDueDateValue());

        CreateTaskDtoService withAllFields = new CreateTaskDtoService(
                userId, 4L, "Full Task", "Detailed", "HIGH", dueDate
        );
        TaskModel fullModel = TaskMapperService.toTaskModel(withAllFields);

        assertEquals(4L, fullModel.getCategoryIdValue());
        assertEquals("Detailed", fullModel.getDescriptionValue());
        assertEquals(TaskPriorityEnum.HIGH, fullModel.getPriorityValue());
        assertEquals(dueDate, fullModel.getDueDateValue());
    }

    @Test
    @DisplayName("TaskMapperRepository should map bidirectionally between TaskModel and TaskEntity")
    void shouldMapBidirectionallyInTaskMapperRepository() {
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.of(2026, 7, 1, 10, 0);
        LocalDateTime dueDate = LocalDateTime.of(2026, 7, 15, 18, 0);

        TaskModel model = TaskModel.reconstitute(
                TaskId.of(25L),
                TaskUserId.of(userId),
                TaskCategoryId.of(4L),
                TaskTitle.of("Arquitectura"),
                TaskDescription.of("Documentar módulos"),
                TaskStatus.inProgress(),
                TaskPriority.high(),
                TaskDueDate.of(dueDate),
                null,
                now,
                now
        );

        TaskEntity entity = TaskMapperRepository.toEntity(model);
        assertEquals(25L, entity.getId());
        assertEquals(userId, entity.getUserId());
        assertEquals(4L, entity.getCategoryId());
        assertEquals("Arquitectura", entity.getTitle());
        assertEquals("Documentar módulos", entity.getDescription());
        assertEquals("IN_PROGRESS", entity.getStatus());
        assertEquals("HIGH", entity.getPriority());
        assertEquals(dueDate, entity.getDueDate());
        assertNull(entity.getCompletedAt());

        TaskModel reconstituted = TaskMapperRepository.toModel(entity);
        assertEquals(25L, reconstituted.getIdValue());
        assertEquals(userId, reconstituted.getUserIdValue());
        assertEquals(4L, reconstituted.getCategoryIdValue());
        assertEquals("Arquitectura", reconstituted.getTitleValue());
        assertEquals("Documentar módulos", reconstituted.getDescriptionValue());
        assertEquals(TaskStatusEnum.IN_PROGRESS, reconstituted.getStatusValue());
        assertEquals(TaskPriorityEnum.HIGH, reconstituted.getPriorityValue());
        assertEquals(dueDate, reconstituted.getDueDateValue());

        // Verify nullable fields on entity
        entity.setCategoryId(null);
        entity.setDescription(null);
        entity.setDueDate(null);
        TaskModel withNullables = TaskMapperRepository.toModel(entity);
        assertNull(withNullables.getCategoryIdValue());
        assertNull(withNullables.getDescriptionValue());
        assertNull(withNullables.getDueDateValue());
    }
}

