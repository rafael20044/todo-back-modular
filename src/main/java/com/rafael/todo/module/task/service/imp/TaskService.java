package com.rafael.todo.module.task.service.imp;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rafael.todo.module.category.service.IFindCategoryUseCase;
import com.rafael.todo.module.task.exception.TaskIdException;
import com.rafael.todo.module.task.model.TaskModel;
import com.rafael.todo.module.task.model.enums.TaskStatusEnum;
import com.rafael.todo.module.task.model.vo.TaskCategoryId;
import com.rafael.todo.module.task.model.vo.TaskDescription;
import com.rafael.todo.module.task.model.vo.TaskDueDate;
import com.rafael.todo.module.task.model.vo.TaskPriority;
import com.rafael.todo.module.task.model.vo.TaskStatus;
import com.rafael.todo.module.task.model.vo.TaskTitle;
import com.rafael.todo.module.task.repository.adapter.ICreateTaskAdapter;
import com.rafael.todo.module.task.repository.adapter.IDeleteTaskAdapter;
import com.rafael.todo.module.task.repository.adapter.IFindTaskAdapter;
import com.rafael.todo.module.task.repository.adapter.IUpdateTaskAdapter;
import com.rafael.todo.module.task.service.ICreateTaskUseCase;
import com.rafael.todo.module.task.service.IDeleteTaskUseCase;
import com.rafael.todo.module.task.service.IFindTaskUseCase;
import com.rafael.todo.module.task.service.IUpdateTaskUseCase;
import com.rafael.todo.module.task.service.dto.CreateTaskDtoService;
import com.rafael.todo.module.task.service.dto.UpdateTaskDtoService;
import com.rafael.todo.module.task.service.mapper.TaskMapperService;
import com.rafael.todo.module.user.service.IFindUserUseCase;

import lombok.extern.java.Log;

@Service 
@Log
public final class TaskService implements 
    ICreateTaskUseCase, 
    IUpdateTaskUseCase, 
    IDeleteTaskUseCase, 
    IFindTaskUseCase {

    private final ICreateTaskAdapter createTaskAdapter;
    private final IUpdateTaskAdapter updateTaskAdapter;
    private final IDeleteTaskAdapter deleteTaskAdapter;
    private final IFindTaskAdapter findTaskAdapter;
    private final IFindUserUseCase findUserUseCase;
    private final IFindCategoryUseCase findCategoryUseCase;

    @Autowired
    public TaskService(
        ICreateTaskAdapter createTaskAdapter, 
        IUpdateTaskAdapter updateTaskAdapter, 
        IDeleteTaskAdapter deleteTaskAdapter, 
        IFindTaskAdapter findTaskAdapter, 
        IFindUserUseCase findUserUseCase,
        IFindCategoryUseCase findCategoryUseCase
    ) {
        this.createTaskAdapter = createTaskAdapter;
        this.updateTaskAdapter = updateTaskAdapter;
        this.deleteTaskAdapter = deleteTaskAdapter;
        this.findTaskAdapter = findTaskAdapter;
        this.findUserUseCase = findUserUseCase;
        this.findCategoryUseCase = findCategoryUseCase;
    }

    @Override
    public TaskModel findById(Long id) {
        log.info("Finding task with id: " + id);
        return this.findTaskAdapter.findById(id).orElseThrow(() -> {
            log.warning("Task not found with id: " + id);
            return TaskIdException.notFound();
        });
    }

    @Override
    public List<TaskModel> findAll(UUID userId, Long categoryId, TaskStatusEnum status) {
        log.info(String.format("Finding tasks with filters [userId: %s, categoryId: %s, status: %s]", userId, categoryId, status));
        return this.findTaskAdapter.findAll(userId, categoryId, status);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting task with id: " + id);
        this.deleteTaskAdapter.delete(id);
    }

    @Override
    public TaskModel update(UpdateTaskDtoService dto) {
        log.info("Updating task with id: " + dto.id());
        var model = findById(dto.id());
        
        if (dto.categoryId() != null) {
            this.validCategoryId(dto.categoryId());
        }

        model = model.changeCategory(dto.categoryId() == null ? model.getCategoryId() : TaskCategoryId.of(dto.categoryId()))
                .changeDescription(dto.description() == null ? model.getDescription() : TaskDescription.of(dto.description()))
                .changeDueDate(dto.dueDate() == null ? model.getDueDate() : TaskDueDate.of(dto.dueDate()))
                .changePriority(dto.priority() == null ? model.getPriority() : TaskPriority.of(dto.priority()))
                .changeStatus(dto.status() == null ? model.getStatus() : TaskStatus.of(dto.status()))
                .changeTitle(dto.title() == null ? model.getTitle() : TaskTitle.of(dto.title()));
        return this.updateTaskAdapter.update(model);
    }

    @Override
    public TaskModel create(CreateTaskDtoService dto) {
        log.info("Creating task: '" + dto.title() + "' for user: " + dto.userId());
        this.validUserId(dto.userId());
        this.validCategoryId(dto.categoryId());
        var model = TaskMapperService.toTaskModel(dto);
        return this.createTaskAdapter.create(model);
    }

    private void validUserId(UUID userId) {
        log.info("Validating existence of user: " + userId);
        this.findUserUseCase.findById(userId);
    }

    private void validCategoryId(Long categoryId) {
        if (categoryId != null) {
            log.info("Validating existence of category: " + categoryId);
            this.findCategoryUseCase.findById(categoryId);
        }
    }

}
