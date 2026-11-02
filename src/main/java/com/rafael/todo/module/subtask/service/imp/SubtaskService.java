package com.rafael.todo.module.subtask.service.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rafael.todo.module.subtask.exception.SubtaskIdException;
import com.rafael.todo.module.subtask.model.SubtaskModel;
import com.rafael.todo.module.subtask.model.vo.SubtaskPosition;
import com.rafael.todo.module.subtask.model.vo.SubtaskTitle;
import com.rafael.todo.module.subtask.repository.adapter.ICreateSubtaskAdapter;
import com.rafael.todo.module.subtask.repository.adapter.IDeleteSubtaskAdapter;
import com.rafael.todo.module.subtask.repository.adapter.IFindSubtaskAdapter;
import com.rafael.todo.module.subtask.repository.adapter.IUpdateSubtaskAdapter;
import com.rafael.todo.module.subtask.service.ICreateSubtaskUseCase;
import com.rafael.todo.module.subtask.service.IDeleteSubtaskUseCase;
import com.rafael.todo.module.subtask.service.IFindSubtaskUseCase;
import com.rafael.todo.module.subtask.service.IUpdateSubtaskUseCase;
import com.rafael.todo.module.subtask.service.dto.CreateSubtaskDtoService;
import com.rafael.todo.module.subtask.service.dto.UpdateSubtaskDtoService;
import com.rafael.todo.module.subtask.service.mapper.SubtaskMapperService;
import com.rafael.todo.module.task.service.IFindTaskUseCase;

import lombok.extern.java.Log;

@Service
@Log
public final class SubtaskService implements
    ICreateSubtaskUseCase,
    IUpdateSubtaskUseCase,
    IDeleteSubtaskUseCase,
    IFindSubtaskUseCase {

    private final ICreateSubtaskAdapter createSubtaskAdapter;
    private final IUpdateSubtaskAdapter updateSubtaskAdapter;
    private final IDeleteSubtaskAdapter deleteSubtaskAdapter;
    private final IFindSubtaskAdapter findSubtaskAdapter;
    private final IFindTaskUseCase findTaskUseCase;

    @Autowired
    public SubtaskService(
        ICreateSubtaskAdapter createSubtaskAdapter,
        IUpdateSubtaskAdapter updateSubtaskAdapter,
        IDeleteSubtaskAdapter deleteSubtaskAdapter,
        IFindSubtaskAdapter findSubtaskAdapter,
        IFindTaskUseCase findTaskUseCase
    ) {
        this.createSubtaskAdapter = createSubtaskAdapter;
        this.updateSubtaskAdapter = updateSubtaskAdapter;
        this.deleteSubtaskAdapter = deleteSubtaskAdapter;
        this.findSubtaskAdapter = findSubtaskAdapter;
        this.findTaskUseCase = findTaskUseCase;
    }

    @Override
    public SubtaskModel create(CreateSubtaskDtoService dto) {
        log.info("Creating subtask: '" + dto.title() + "' for task: " + dto.taskId());
        this.validTaskId(dto.taskId());
        var model = SubtaskMapperService.toSubtaskModel(dto);
        return this.createSubtaskAdapter.create(model);
    }

    @Override
    public SubtaskModel findById(Long id) {
        log.info("Finding subtask with id: " + id);
        return this.findSubtaskAdapter.findById(id).orElseThrow(() -> {
            log.warning("Subtask not found with id: " + id);
            return SubtaskIdException.notFound();
        });
    }

    @Override
    public List<SubtaskModel> findAllByTaskId(Long taskId) {
        log.info("Finding subtasks for task: " + taskId);
        this.validTaskId(taskId);
        return this.findSubtaskAdapter.findAllByTaskId(taskId);
    }

    @Override
    public SubtaskModel update(UpdateSubtaskDtoService dto) {
        log.info("Updating subtask with id: " + dto.id());
        var model = findById(dto.id());

        if (dto.title() != null) {
            model = model.changeTitle(SubtaskTitle.of(dto.title()));
        }
        if (dto.isCompleted() != null) {
            model = dto.isCompleted() ? model.markCompleted() : model.markPending();
        }
        if (dto.position() != null) {
            model = model.changePosition(SubtaskPosition.of(dto.position()));
        }

        return this.updateSubtaskAdapter.update(model);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting subtask with id: " + id);
        this.deleteSubtaskAdapter.delete(id);
    }

    private void validTaskId(Long taskId) {
        log.info("Validating existence of task: " + taskId);
        this.findTaskUseCase.findById(taskId);
    }
}

