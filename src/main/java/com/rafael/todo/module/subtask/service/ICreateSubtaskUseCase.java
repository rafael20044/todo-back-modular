package com.rafael.todo.module.subtask.service;

import com.rafael.todo.module.subtask.model.SubtaskModel;
import com.rafael.todo.module.subtask.service.dto.CreateSubtaskDtoService;

public interface ICreateSubtaskUseCase {
    SubtaskModel create(CreateSubtaskDtoService dto);
}

