package com.rafael.todo.module.task.service;

import com.rafael.todo.module.task.model.TaskModel;
import com.rafael.todo.module.task.service.dto.UpdateTaskDtoService;

public interface IUpdateTaskUseCase {
    TaskModel update(UpdateTaskDtoService dto);
}
