package com.rafael.todo.module.task.repository.adapter;

import com.rafael.todo.module.task.model.TaskModel;

public interface ICreateTaskAdapter {
    TaskModel create(TaskModel task);
}
