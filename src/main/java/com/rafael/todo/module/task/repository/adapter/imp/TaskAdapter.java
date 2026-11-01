package com.rafael.todo.module.task.repository.adapter.imp;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rafael.todo.module.task.model.TaskModel;
import com.rafael.todo.module.task.model.enums.TaskStatusEnum;
import com.rafael.todo.module.task.repository.ITaskRepository;
import com.rafael.todo.module.task.repository.adapter.ICreateTaskAdapter;
import com.rafael.todo.module.task.repository.adapter.IDeleteTaskAdapter;
import com.rafael.todo.module.task.repository.adapter.IFindTaskAdapter;
import com.rafael.todo.module.task.repository.adapter.IUpdateTaskAdapter;
import com.rafael.todo.module.task.repository.mapper.TaskMapperRepository;

import lombok.extern.java.Log;

@Component 
@Log
public final class TaskAdapter implements 
    ICreateTaskAdapter, 
    IUpdateTaskAdapter, 
    IFindTaskAdapter, 
    IDeleteTaskAdapter {

    private final ITaskRepository repo;

    @Autowired 
    public TaskAdapter(ITaskRepository repo) {
        this.repo = repo;
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting task entity by id: " + id);
        this.repo.deleteById(id);
    }

    @Override
    public Optional<TaskModel> findById(Long id) {
        log.info("Querying task by id: " + id);
        return this.repo.findById(id).map(TaskMapperRepository::toModel);
    }

    @Override
    public List<TaskModel> findAll(UUID userId, Long categoryId, TaskStatusEnum status) {
        var statusStr = status == null ? null : status.getValue();
        log.info(String.format("Querying tasks with filters [userId: %s, categoryId: %s, status: %s]", userId, categoryId, statusStr));
        return this.repo.findAllFiltered(userId, categoryId, statusStr).stream().map(TaskMapperRepository::toModel).toList();
    }

    @Override
    public TaskModel update(TaskModel task) {
        log.info("Updating task entity with id: " + task.getIdValue());
        return save(task);
    }

    @Override
    public TaskModel create(TaskModel task) {
        log.info("Persisting new task entity with title: " + task.getTitleValue());
        return save(task);
    }

    private TaskModel save(TaskModel task) {
        var entity = TaskMapperRepository.toEntity(task);
        var saved = this.repo.save(entity);
        return TaskMapperRepository.toModel(saved);
    }

}
