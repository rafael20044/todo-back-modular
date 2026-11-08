package com.rafael.todo.module.subtask.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import com.rafael.todo.module.subtask.controller.dto.CreateSubtaskDto;
import com.rafael.todo.module.subtask.controller.dto.ResponseSubtaskDto;
import com.rafael.todo.module.subtask.controller.dto.UpdateSubtaskDto;
import com.rafael.todo.module.subtask.controller.mapper.SubtaskMapperController;
import com.rafael.todo.module.subtask.model.SubtaskModel;
import com.rafael.todo.module.subtask.model.vo.SubtaskCompleted;
import com.rafael.todo.module.subtask.model.vo.SubtaskId;
import com.rafael.todo.module.subtask.model.vo.SubtaskPosition;
import com.rafael.todo.module.subtask.model.vo.SubtaskTaskId;
import com.rafael.todo.module.subtask.model.vo.SubtaskTitle;
import com.rafael.todo.module.subtask.repository.entity.SubtaskEntity;
import com.rafael.todo.module.subtask.repository.mapper.SubtaskMapperRepository;
import com.rafael.todo.module.subtask.service.dto.CreateSubtaskDtoService;
import com.rafael.todo.module.subtask.service.dto.UpdateSubtaskDtoService;
import com.rafael.todo.module.subtask.service.mapper.SubtaskMapperService;

@DisplayName("Subtask Mappers (Controller, Service, Repository) - Unit Tests")
class SubtaskMappersTest {

    @Test
    @DisplayName("SubtaskMapperController should map DTOs, lists and pages accurately")
    void shouldMapInSubtaskMapperController() {
        CreateSubtaskDto createDto = new CreateSubtaskDto(10L, "Subtarea 1", 1);
        CreateSubtaskDtoService serviceCreateDto = SubtaskMapperController.toServiceDto(createDto);

        assertEquals(10L, serviceCreateDto.taskId());
        assertEquals("Subtarea 1", serviceCreateDto.title());
        assertEquals(1, serviceCreateDto.position());

        UpdateSubtaskDto updateDto = new UpdateSubtaskDto("Subtarea Editada", true, 2);
        UpdateSubtaskDtoService serviceUpdateDto = SubtaskMapperController.toServiceDto(100L, updateDto);

        assertEquals(100L, serviceUpdateDto.id());
        assertEquals("Subtarea Editada", serviceUpdateDto.title());
        assertTrue(serviceUpdateDto.isCompleted());
        assertEquals(2, serviceUpdateDto.position());

        LocalDateTime now = LocalDateTime.of(2026, 9, 1, 10, 0);
        SubtaskModel model = SubtaskModel.reconstitute(
                SubtaskId.of(100L),
                SubtaskTaskId.of(10L),
                SubtaskTitle.of("Subtarea Editada"),
                SubtaskCompleted.completed(),
                SubtaskPosition.of(2),
                now,
                now
        );

        ResponseSubtaskDto responseDto = SubtaskMapperController.toResponseDto(model);
        assertEquals(100L, responseDto.id());
        assertEquals(10L, responseDto.taskId());
        assertEquals("Subtarea Editada", responseDto.title());
        assertTrue(responseDto.isCompleted());
        assertEquals(2, responseDto.position());
        assertEquals(now, responseDto.createdAt());
        assertEquals(now, responseDto.updatedAt());

        List<ResponseSubtaskDto> list = SubtaskMapperController.toResponseList(List.of(model));
        assertEquals(1, list.size());

        Page<ResponseSubtaskDto> page = SubtaskMapperController.toPage(List.of(model), 0, 20);
        assertEquals(1, page.getTotalElements());
        assertEquals("Subtarea Editada", page.getContent().getFirst().title());
    }

    @Test
    @DisplayName("SubtaskMapperService should map CreateSubtaskDtoService with default or custom position")
    void shouldMapInSubtaskMapperService() {
        CreateSubtaskDtoService withNullPosition = new CreateSubtaskDtoService(10L, "Paso 1", null);
        SubtaskModel defaultPosModel = SubtaskMapperService.toSubtaskModel(withNullPosition);

        assertNull(defaultPosModel.getIdValue());
        assertEquals(10L, defaultPosModel.getTaskIdValue());
        assertEquals("Paso 1", defaultPosModel.getTitleValue());
        assertFalse(defaultPosModel.isCompletedValue());
        assertEquals(0, defaultPosModel.getPositionValue());

        CreateSubtaskDtoService withCustomPosition = new CreateSubtaskDtoService(10L, "Paso 2", 4);
        SubtaskModel customPosModel = SubtaskMapperService.toSubtaskModel(withCustomPosition);

        assertEquals(4, customPosModel.getPositionValue());
    }

    @Test
    @DisplayName("SubtaskMapperRepository should map bidirectionally between SubtaskModel and SubtaskEntity")
    void shouldMapBidirectionallyInSubtaskMapperRepository() {
        LocalDateTime now = LocalDateTime.of(2026, 9, 10, 14, 0);
        SubtaskModel model = SubtaskModel.reconstitute(
                SubtaskId.of(55L),
                SubtaskTaskId.of(10L),
                SubtaskTitle.of("Verificar datos"),
                SubtaskCompleted.completed(),
                SubtaskPosition.of(1),
                now,
                now
        );

        SubtaskEntity entity = SubtaskMapperRepository.toEntity(model);
        assertEquals(55L, entity.getId());
        assertEquals(10L, entity.getTaskId());
        assertEquals("Verificar datos", entity.getTitle());
        assertTrue(entity.isCompleted());
        assertEquals(1, entity.getPosition());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());

        SubtaskModel reconstituted = SubtaskMapperRepository.toModel(entity);
        assertEquals(55L, reconstituted.getIdValue());
        assertEquals(10L, reconstituted.getTaskIdValue());
        assertEquals("Verificar datos", reconstituted.getTitleValue());
        assertTrue(reconstituted.isCompletedValue());
        assertEquals(1, reconstituted.getPositionValue());
        assertEquals(now, reconstituted.getCreatedAt());
        assertEquals(now, reconstituted.getUpdatedAt());
    }
}

