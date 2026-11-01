package com.rafael.todo.module.task.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rafael.todo.module.task.controller.dto.CreateTaskDto;
import com.rafael.todo.module.task.controller.dto.ResponseTaskDto;
import com.rafael.todo.module.task.controller.dto.UpdateTaskDto;
import com.rafael.todo.module.task.controller.mapper.TaskMapperController;
import com.rafael.todo.module.task.model.enums.TaskStatusEnum;
import com.rafael.todo.module.task.service.ICreateTaskUseCase;
import com.rafael.todo.module.task.service.IDeleteTaskUseCase;
import com.rafael.todo.module.task.service.IFindTaskUseCase;
import com.rafael.todo.module.task.service.IUpdateTaskUseCase;

import lombok.extern.java.Log;

@RestController
@RequestMapping("/tasks")
@Log
public final class TaskController {

    private final ICreateTaskUseCase createTaskUseCase;
    private final IFindTaskUseCase findTaskUseCase;
    private final IUpdateTaskUseCase updateTaskUseCase;
    private final IDeleteTaskUseCase deleteTaskUseCase;

    @Autowired
    public TaskController(
            ICreateTaskUseCase createTaskUseCase,
            IFindTaskUseCase findTaskUseCase,
            IUpdateTaskUseCase updateTaskUseCase,
            IDeleteTaskUseCase deleteTaskUseCase
    ) {
        this.createTaskUseCase = createTaskUseCase;
        this.findTaskUseCase = findTaskUseCase;
        this.updateTaskUseCase = updateTaskUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
    }

    @PostMapping
    public ResponseEntity<ResponseTaskDto> create(@RequestBody CreateTaskDto request) {
        log.info("POST /tasks - Request received to create task: '" + request.title() + "' for user: " + request.userId());
        var model = this.createTaskUseCase.create(TaskMapperController.toServiceDto(request));
        var response = TaskMapperController.toResponseDto(model);
        var uri = URI.create("/tasks/" + response.id());
        log.info("POST /tasks - Task successfully created with ID: " + response.id());
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ResponseTaskDto>> findAll(
        @RequestParam(required = false) UUID userId,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) TaskStatusEnum status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info(String.format("GET /tasks - Listing tasks (userId: %s, categoryId: %s, status: %s, page: %d, size: %d)", userId, categoryId, status, page, size));
        var list = this.findTaskUseCase.findAll(userId, categoryId, status);
        var pages = TaskMapperController.toPage(list, page, size);
        return ResponseEntity.ok(pages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseTaskDto> findById(@PathVariable Long id) {
        log.info("GET /tasks/" + id + " - Fetching task by ID");
        var model = this.findTaskUseCase.findById(id);
        return ResponseEntity.ok(TaskMapperController.toResponseDto(model));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseTaskDto> update(
        @PathVariable Long id, 
        @RequestBody UpdateTaskDto request
    ) {
        log.info("PUT /tasks/" + id + " - Request received to update task");
        var model = this.updateTaskUseCase.update(TaskMapperController.toServiceDto(id, request));
        var response = TaskMapperController.toResponseDto(model);
        log.info("PUT /tasks/" + id + " - Task updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /tasks/" + id + " - Request received to delete task");
        this.deleteTaskUseCase.delete(id);
        log.info("DELETE /tasks/" + id + " - Task deleted successfully");
        return ResponseEntity.noContent().build();
    }

}
