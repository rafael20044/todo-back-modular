package com.rafael.todo.module.subtask.repository.adapter;

import java.util.List;
import java.util.Optional;

import com.rafael.todo.module.subtask.model.SubtaskModel;

public interface IFindSubtaskAdapter {
    Optional<SubtaskModel> findById(Long id);
    List<SubtaskModel> findAllByTaskId(Long taskId);
}

