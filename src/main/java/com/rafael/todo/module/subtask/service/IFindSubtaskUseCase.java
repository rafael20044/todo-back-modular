package com.rafael.todo.module.subtask.service;

import java.util.List;

import com.rafael.todo.module.subtask.model.SubtaskModel;

public interface IFindSubtaskUseCase {
    SubtaskModel findById(Long id);
    List<SubtaskModel> findAllByTaskId(Long taskId);
}

