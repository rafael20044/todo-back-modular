package com.rafael.todo.module.task.controller.mapper;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.rafael.todo.module.task.controller.dto.CreateTaskDto;
import com.rafael.todo.module.task.controller.dto.ResponseTaskDto;
import com.rafael.todo.module.task.controller.dto.UpdateTaskDto;
import com.rafael.todo.module.task.model.TaskModel;
import com.rafael.todo.module.task.service.dto.CreateTaskDtoService;
import com.rafael.todo.module.task.service.dto.UpdateTaskDtoService;

public final class TaskMapperController {

    private TaskMapperController() {}

    public static CreateTaskDtoService toServiceDto(CreateTaskDto dto) {
        return new CreateTaskDtoService(
            dto.userId(),
            dto.categoryId(),
            dto.title(),
            dto.description(),
            dto.priority(),
            dto.dueDate()
        );
    }

    public static UpdateTaskDtoService toServiceDto(Long id, UpdateTaskDto dto) {
        return new UpdateTaskDtoService(
            id,
            dto.categoryId(),
            dto.title(),
            dto.description(),
            dto.status(),
            dto.priority(),
            dto.dueDate()
        );
    }

    public static ResponseTaskDto toResponseDto(TaskModel model) {
        return new ResponseTaskDto(
            model.getIdValue(),
            model.getUserIdValue(),
            model.getCategoryIdValue(),
            model.getTitleValue(),
            model.getDescriptionValue(),
            model.getStatusValue().getValue(),
            model.getPriorityValue().getValue(),
            model.getDueDateValue(),
            model.getCompletedAt(),
            model.getCreatedAt(),
            model.getUpdatedAt()
        );
    }

    public static List<ResponseTaskDto> toResponseList(List<TaskModel> models) {
        return models.stream().map(TaskMapperController::toResponseDto).toList();
    }

    public static Page<ResponseTaskDto> toPage(List<TaskModel> tasks, int page, int size) {
        var pageable = PageRequest.of(page, size);
        var pages = new PageImpl<>(tasks, pageable, tasks.size());
        return pages.map(TaskMapperController::toResponseDto);
    }

}

