package com.rafael.todo.module.task.service;

import java.util.List;
import java.util.UUID;

import com.rafael.todo.module.task.model.TaskModel;
import com.rafael.todo.module.task.model.enums.TaskStatusEnum;

public interface IFindTaskUseCase {
    TaskModel findById(Long id);
    List<TaskModel> findAll(UUID userId, Long categoryId, TaskStatusEnum status);
}
