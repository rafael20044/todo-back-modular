package com.rafael.todo.module.task.repository.adapter.imp;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.rafael.todo.module.task.model.TaskModel;
import com.rafael.todo.module.task.model.enums.TaskStatusEnum;
import com.rafael.todo.module.task.repository.ITaskRepository;
import com.rafael.todo.module.task.repository.adapter.ICreateTaskAdapter;
import com.rafael.todo.module.task.repository.adapter.IDeleteTaskAdapter;
import com.rafael.todo.module.task.repository.adapter.IFindTaskAdapter;
import com.rafael.todo.module.task.repository.adapter.IUpdateTaskAdapter;
import com.rafael.todo.module.task.repository.mapper.TaskMapperRepository;


public class TaskAdapter implements 
    ICreateTaskAdapter, 
    IUpdateTaskAdapter, 
    IFindTaskAdapter, 
    IDeleteTaskAdapter{

    private final ITaskRepository repo;


    @Autowired 
    public TaskAdapter(ITaskRepository repo) {
        this.repo = repo;
    }

    @Override
    public void delete(Long id) {
        this.repo.deleteById(id);
    }

    @Override
    public Optional<TaskModel> findById(Long id) {
        return this.repo.findById(id).map(TaskMapperRepository::toModel);
    }

    @Override
    public List<TaskModel> findAll(UUID userId, Long categoryId, TaskStatusEnum status) {
        return this.repo.findAllFiltered(userId, categoryId, status.getValue()).stream().map(TaskMapperRepository::toModel).toList();
    }

    @Override
    public TaskModel update(TaskModel task) {
        return save(task);
    }

    @Override
    public TaskModel create(TaskModel task) {
        return save(task);
    }

    private TaskModel save(TaskModel task){
        var entity = TaskMapperRepository.toEntity(task);
        return TaskMapperRepository.toModel(repo.save(entity));
    }


}
