package com.rafael.todo.module.task.service;

import com.rafael.todo.module.task.model.TaskModel;
import com.rafael.todo.module.task.service.dto.CreateTaskDtoService;

public interface ICreateTaskUseCase {
    TaskModel create(CreateTaskDtoService dto);
}
