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

@RestController
@RequestMapping("/tasks")
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
        var model = this.createTaskUseCase.create(TaskMapperController.toServiceDto(request));
        var uri = URI.create("/tasks/" + model.getId());
        return ResponseEntity.created(uri).body(TaskMapperController.toResponseDto(model));
    }

    @GetMapping
    public ResponseEntity<Page<ResponseTaskDto>> findAll(
        @RequestParam(required = false) UUID userId,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) TaskStatusEnum status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        var list = this.findTaskUseCase.findAll(userId, categoryId, status);
        return ResponseEntity.ok(TaskMapperController.toPage(list, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseTaskDto> findById(@PathVariable Long id) {
        var model = this.findTaskUseCase.findById(id);
        return ResponseEntity.ok(TaskMapperController.toResponseDto(model));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseTaskDto> update(
        @PathVariable Long id, 
        @RequestBody UpdateTaskDto request
    ) {
        var model = this.updateTaskUseCase.update(TaskMapperController.toServiceDto(id, request));
        return ResponseEntity.ok(TaskMapperController.toResponseDto(model));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.deleteTaskUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

}
