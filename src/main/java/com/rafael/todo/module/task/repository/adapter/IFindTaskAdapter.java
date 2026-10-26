package com.rafael.todo.module.task.repository.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.rafael.todo.module.task.model.TaskModel;
import com.rafael.todo.module.task.model.enums.TaskStatusEnum;

public interface IFindTaskAdapter {
    Optional<TaskModel> findById(Long id);
    List<TaskModel> findAll(UUID userId, Long categoryId, TaskStatusEnum status);
}
