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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.java.Log;

@RestController
@RequestMapping("/subtasks")
@Tag(name = "5. Subtareas (Subtasks)", description = "Gestión de elementos de checklist asociados a una tarea")
@Log
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
    @Operation(summary = "Crear subtarea", description = "Crea una subtarea asociada a una tarea existente")
    public ResponseEntity<ResponseSubtaskDto> create(@RequestBody CreateSubtaskDto request) {
        log.info("POST /subtasks - Request received to create subtask: '" + request.title() + "' for task: " + request.taskId());
        var model = this.createSubtaskUseCase.create(SubtaskMapperController.toServiceDto(request));
        var dto = SubtaskMapperController.toResponseDto(model);
        var uri = URI.create("subtasks/" + dto.id());
        log.info("POST /subtasks - Subtask successfully created with ID: " + dto.id());
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    @Operation(summary = "Listar subtareas de una tarea", description = "Obtiene las subtareas paginadas y ordenadas por posición para un taskId")
    public ResponseEntity<Page<ResponseSubtaskDto>> findAll(
        @RequestParam Long taskId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info(String.format("GET /subtasks - Listing subtasks (taskId: %d, page: %d, size: %d)", taskId, page, size));
        var list = this.findSubtaskUseCase.findAllByTaskId(taskId);
        return ResponseEntity.ok(SubtaskMapperController.toPage(list, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener subtarea por ID", description = "Busca una subtarea específica por su ID")
    public ResponseEntity<ResponseSubtaskDto> findById(@PathVariable Long id) {
        log.info("GET /subtasks/" + id + " - Fetching subtask by ID");
        var model = this.findSubtaskUseCase.findById(id);
        return ResponseEntity.ok(SubtaskMapperController.toResponseDto(model));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar subtarea", description = "Modifica el título, estado completado o posición de una subtarea")
    public ResponseEntity<ResponseSubtaskDto> update(
        @PathVariable Long id,
        @RequestBody UpdateSubtaskDto request
    ) {
        log.info("PUT /subtasks/" + id + " - Request received to update subtask");
        var model = this.updateSubtaskUseCase.update(SubtaskMapperController.toServiceDto(id, request));
        var response = SubtaskMapperController.toResponseDto(model);
        log.info("PUT /subtasks/" + id + " - Subtask updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar subtarea", description = "Elimina una subtarea por su ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /subtasks/" + id + " - Request received to delete subtask");
        this.deleteSubtaskUseCase.delete(id);
        log.info("DELETE /subtasks/" + id + " - Subtask deleted successfully");
        return ResponseEntity.noContent().build();
    }

}
