package com.rafael.todo.module.category.controller;

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

import com.rafael.todo.module.category.controller.dto.CreateCategoryDto;
import com.rafael.todo.module.category.controller.dto.ResponseCategoryDto;
import com.rafael.todo.module.category.controller.dto.UpdateCategoryDto;
import com.rafael.todo.module.category.controller.mapper.CategoryMapperController;
import com.rafael.todo.module.category.service.ICreateCategoryUseCase;
import com.rafael.todo.module.category.service.IDeleteCategoryUseCase;
import com.rafael.todo.module.category.service.IFindCategoryUseCase;
import com.rafael.todo.module.category.service.IUpdateCategoryUseCase;

@RestController 
@RequestMapping ("/categories")
public final class CategoryController {

    private final IFindCategoryUseCase findCategoryUseCase;
    private final ICreateCategoryUseCase createCategoryUseCase;
    private final IUpdateCategoryUseCase updateCategoryUseCase;
    private final IDeleteCategoryUseCase deleteCategoryUseCase;

    @Autowired 
    public CategoryController(
            IFindCategoryUseCase findCategoryUseCase,
            ICreateCategoryUseCase createCategoryUseCase,
            IUpdateCategoryUseCase updateCategoryUseCase,
            IDeleteCategoryUseCase deleteCategoryUseCase) {
        this.findCategoryUseCase = findCategoryUseCase;
        this.createCategoryUseCase = createCategoryUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
    }

    @PostMapping
    public ResponseEntity<ResponseCategoryDto> create(@RequestBody CreateCategoryDto request) {
        var model = this.createCategoryUseCase.create(CategoryMapperController.toServiceDto(request));
        var uri = URI.create("/categories/" + model.getIdValue());
        return ResponseEntity.created(uri).body(CategoryMapperController.toResponseDto(model));
    }

    @GetMapping 
    public ResponseEntity<Page<ResponseCategoryDto>> findAll(
        @RequestParam(required = false) UUID userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        var list = this.findCategoryUseCase.findAll(userId);
        var pages = CategoryMapperController.toPage(list, page, size);

        return ResponseEntity.ok(pages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseCategoryDto> findById(@PathVariable Long id) {
        var model = this.findCategoryUseCase.findById(id);
        return ResponseEntity.ok(CategoryMapperController.toResponseDto(model));
    }

    @PutMapping ("/{id}")
    public ResponseEntity<ResponseCategoryDto> update(
        @PathVariable Long id, 
        @RequestBody UpdateCategoryDto request
    ) {
        var model = this.updateCategoryUseCase.update(CategoryMapperController.toServiceDto(id, request));
        return ResponseEntity.ok(CategoryMapperController.toResponseDto(model));
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<ResponseCategoryDto> delete(@PathVariable Long id) {
        this.deleteCategoryUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

}
