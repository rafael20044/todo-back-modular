package com.rafael.todo.module.subtask.repository.adapter.imp;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rafael.todo.module.subtask.model.SubtaskModel;
import com.rafael.todo.module.subtask.repository.ISubtaskRepository;
import com.rafael.todo.module.subtask.repository.adapter.ICreateSubtaskAdapter;
import com.rafael.todo.module.subtask.repository.adapter.IDeleteSubtaskAdapter;
import com.rafael.todo.module.subtask.repository.adapter.IFindSubtaskAdapter;
import com.rafael.todo.module.subtask.repository.adapter.IUpdateSubtaskAdapter;
import com.rafael.todo.module.subtask.repository.mapper.SubtaskMapperRepository;

import lombok.extern.java.Log;

@Component 
@Log
public final class SubtaskAdapter implements 
    ICreateSubtaskAdapter, 
    IUpdateSubtaskAdapter, 
    IFindSubtaskAdapter, 
    IDeleteSubtaskAdapter {

    private final ISubtaskRepository repository;

    @Autowired 
    public SubtaskAdapter(ISubtaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting subtask entity by id: " + id);
        this.repository.deleteById(id);
    }

    @Override
    public Optional<SubtaskModel> findById(Long id) {
        log.info("Querying subtask by id: " + id);
        return this.repository.findById(id).map(SubtaskMapperRepository::toModel);
    }

    @Override
    public List<SubtaskModel> findAllByTaskId(Long taskId) {
        log.info("Querying subtasks by taskId: " + taskId);
        return this.repository.findAllByTaskIdOrderByPositionAsc(taskId).stream().map(SubtaskMapperRepository::toModel).toList();
    }

    @Override
    public SubtaskModel update(SubtaskModel subtask) {
        log.info("Updating subtask entity with id: " + subtask.getIdValue());
        return save(subtask);
    }

    @Override
    public SubtaskModel create(SubtaskModel subtask) {
        log.info("Persisting new subtask entity with title: " + subtask.getTitleValue());
        return save(subtask);
    }

    private SubtaskModel save(SubtaskModel subTask){
        var entity = SubtaskMapperRepository.toEntity(subTask);
        return SubtaskMapperRepository.toModel(repository.save(entity));
    }

}
