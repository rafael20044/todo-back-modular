package com.rafael.todo.module.subtask.service;

import com.rafael.todo.module.subtask.model.SubtaskModel;
import com.rafael.todo.module.subtask.service.dto.UpdateSubtaskDtoService;

public interface IUpdateSubtaskUseCase {
    SubtaskModel update(UpdateSubtaskDtoService dto);
}

