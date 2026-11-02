package com.rafael.todo.module.subtask.service.mapper;

import com.rafael.todo.module.subtask.model.SubtaskModel;
import com.rafael.todo.module.subtask.model.vo.SubtaskPosition;
import com.rafael.todo.module.subtask.model.vo.SubtaskTaskId;
import com.rafael.todo.module.subtask.model.vo.SubtaskTitle;
import com.rafael.todo.module.subtask.service.dto.CreateSubtaskDtoService;

public final class SubtaskMapperService {

    private SubtaskMapperService() {}

    public static SubtaskModel toSubtaskModel(CreateSubtaskDtoService dto) {
        return SubtaskModel.create(
            SubtaskTaskId.of(dto.taskId()),
            SubtaskTitle.of(dto.title()),
            dto.position() != null ? SubtaskPosition.of(dto.position()) : SubtaskPosition.defaultPosition()
        );
    }

}

