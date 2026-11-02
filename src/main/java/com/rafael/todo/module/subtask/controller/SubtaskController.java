package com.rafael.todo.module.subtask.controller;

import java.net.URI;

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

import com.rafael.todo.module.subtask.controller.dto.CreateSubtaskDto;
import com.rafael.todo.module.subtask.controller.dto.ResponseSubtaskDto;
import com.rafael.todo.module.subtask.controller.dto.UpdateSubtaskDto;
import com.rafael.todo.module.subtask.controller.mapper.SubtaskMapperController;
import com.rafael.todo.module.subtask.service.ICreateSubtaskUseCase;
import com.rafael.todo.module.subtask.service.IDeleteSubtaskUseCase;
import com.rafael.todo.module.subtask.service.IFindSubtaskUseCase;
import com.rafael.todo.module.subtask.service.IUpdateSubtaskUseCase;

@RestController
@RequestMapping("/subtasks")
public final class SubtaskController {

    private final ICreateSubtaskUseCase createSubtaskUseCase;
    private final IFindSubtaskUseCase findSubtaskUseCase;
    private final IUpdateSubtaskUseCase updateSubtaskUseCase;
    private final IDeleteSubtaskUseCase deleteSubtaskUseCase;

    @Autowired
    public SubtaskController(
        ICreateSubtaskUseCase createSubtaskUseCase,
        IFindSubtaskUseCase findSubtaskUseCase,
        IUpdateSubtaskUseCase updateSubtaskUseCase,
        IDeleteSubtaskUseCase deleteSubtaskUseCase
    ) {
        this.createSubtaskUseCase = createSubtaskUseCase;
        this.findSubtaskUseCase = findSubtaskUseCase;
        this.updateSubtaskUseCase = updateSubtaskUseCase;
        this.deleteSubtaskUseCase = deleteSubtaskUseCase;
    }

    @PostMapping
    public ResponseEntity<ResponseSubtaskDto> create(@RequestBody CreateSubtaskDto request) {
        var model = this.createSubtaskUseCase.create(SubtaskMapperController.toServiceDto(request));
        var dto = SubtaskMapperController.toResponseDto(model);
        var uri = URI.create("subtasks/" + dto.id());
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    public ResponseEntity<Page<ResponseSubtaskDto>> findAll(
        @RequestParam Long taskId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        var list = this.findSubtaskUseCase.findAllByTaskId(taskId);
        return ResponseEntity.ok(SubtaskMapperController.toPage(list, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseSubtaskDto> findById(@PathVariable Long id) {
        var model = this.findSubtaskUseCase.findById(id);
        return ResponseEntity.ok(SubtaskMapperController.toResponseDto(model));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseSubtaskDto> update(
        @PathVariable Long id,
        @RequestBody UpdateSubtaskDto request
    ) {
        var model = this.updateSubtaskUseCase.update(SubtaskMapperController.toServiceDto(id, request));
        return ResponseEntity.ok(SubtaskMapperController.toResponseDto(model));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.deleteSubtaskUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

}

