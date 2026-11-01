package com.rafael.todo.module.task.controller;

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
        return null;
    }

    @GetMapping
    public ResponseEntity<Page<ResponseTaskDto>> findAll(
        @RequestParam(required = false) UUID userId,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) TaskStatusEnum status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseTaskDto> findById(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseTaskDto> update(
        @PathVariable Long id, 
        @RequestBody UpdateTaskDto request
    ) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return null;
    }

}
