package com.rafael.todo.module.subtask.controller.mapper;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.rafael.todo.module.subtask.controller.dto.CreateSubtaskDto;
import com.rafael.todo.module.subtask.controller.dto.ResponseSubtaskDto;
import com.rafael.todo.module.subtask.controller.dto.UpdateSubtaskDto;
import com.rafael.todo.module.subtask.model.SubtaskModel;
import com.rafael.todo.module.subtask.service.dto.CreateSubtaskDtoService;
import com.rafael.todo.module.subtask.service.dto.UpdateSubtaskDtoService;

public final class SubtaskMapperController {

    private SubtaskMapperController() {}

    public static CreateSubtaskDtoService toServiceDto(CreateSubtaskDto dto) {
        return new CreateSubtaskDtoService(
            dto.taskId(),
            dto.title(),
            dto.position()
        );
    }

    public static UpdateSubtaskDtoService toServiceDto(Long id, UpdateSubtaskDto dto) {
        return new UpdateSubtaskDtoService(
            id,
            dto.title(),
            dto.isCompleted(),
            dto.position()
        );
    }

    public static ResponseSubtaskDto toResponseDto(SubtaskModel model) {
        return new ResponseSubtaskDto(
            model.getIdValue(),
            model.getTaskIdValue(),
            model.getTitleValue(),
            model.isCompletedValue(),
            model.getPositionValue(),
            model.getCreatedAt(),
            model.getUpdatedAt()
        );
    }

    public static List<ResponseSubtaskDto> toResponseList(List<SubtaskModel> models) {
        return models.stream().map(SubtaskMapperController::toResponseDto).toList();
    }

    public static Page<ResponseSubtaskDto> toPage(List<SubtaskModel> subtasks, int page, int size) {
        var pageable = PageRequest.of(page, size);
        var pages = new PageImpl<>(subtasks, pageable, subtasks.size());
        return pages.map(SubtaskMapperController::toResponseDto);
    }

}

