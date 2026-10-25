package com.rafael.todo.module.category.service.imp;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rafael.todo.module.category.exception.CategoryIdException;
import com.rafael.todo.module.category.model.CategoryModel;
import com.rafael.todo.module.category.model.vo.CategoryColor;
import com.rafael.todo.module.category.model.vo.CategoryName;
import com.rafael.todo.module.category.repository.adapter.ICreateCategoryAdapter;
import com.rafael.todo.module.category.repository.adapter.IDeleteCategoryAdapter;
import com.rafael.todo.module.category.repository.adapter.IFindCategoryAdapter;
import com.rafael.todo.module.category.repository.adapter.IUpdateCategoryAdapter;
import com.rafael.todo.module.category.service.ICreateCategoryUseCase;
import com.rafael.todo.module.category.service.IDeleteCategoryUseCase;
import com.rafael.todo.module.category.service.IFindCategoryUseCase;
import com.rafael.todo.module.category.service.IUpdateCategoryUseCase;
import com.rafael.todo.module.category.service.dto.CreateCategoryDtoService;
import com.rafael.todo.module.category.service.dto.UpdateCategoryDtoService;
import com.rafael.todo.module.category.service.mapper.CategoryMapperService;
import com.rafael.todo.module.user.service.IFindUserUseCase;

import lombok.extern.java.Log;

@Service 
@Log
public final class CategoryService implements 
    ICreateCategoryUseCase, 
    IUpdateCategoryUseCase, 
    IFindCategoryUseCase, 
    IDeleteCategoryUseCase {
    
    private final ICreateCategoryAdapter createCategoryAdapter;
    private final IUpdateCategoryAdapter updateCategoryAdapter;
    private final IFindCategoryAdapter findCategoryAdapter;
    private final IDeleteCategoryAdapter deleteCategoryAdapter;
    private final IFindUserUseCase findUserUseCase;

    @Autowired 
    public CategoryService(
        ICreateCategoryAdapter createCategoryAdapter,
        IUpdateCategoryAdapter updateCategoryAdapter,
        IFindCategoryAdapter findCategoryAdapter,
        IDeleteCategoryAdapter deleteCategoryAdapter,
        IFindUserUseCase findUserUseCase
    ) {
        this.createCategoryAdapter = createCategoryAdapter;
        this.updateCategoryAdapter = updateCategoryAdapter;
        this.findCategoryAdapter = findCategoryAdapter;
        this.deleteCategoryAdapter = deleteCategoryAdapter;
        this.findUserUseCase = findUserUseCase;
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting category with id: " + id);
        this.deleteCategoryAdapter.delete(id);
    }

    @Override
    public CategoryModel findById(Long id) {
        log.info("Finding category with id: " + id);
        var exist = this.findCategoryAdapter.findById(id);
        if (exist.isEmpty()) {
            log.warning("Category not found with id: " + id);
            throw CategoryIdException.notFound();
        }
        return exist.get();
    }

    @Override
    public List<CategoryModel> findAll(UUID userId) {
        log.info("Finding all categories for user: " + userId);
        return this.findCategoryAdapter.findAll(userId);
    }

    @Override
    public CategoryModel update(UpdateCategoryDtoService dto) {
        log.info("Updating category with id: " + dto.id());
        var model = findById(dto.id());

        model = model.changeName(dto.name() == null ? model.getName() : CategoryName.of(dto.name()))
                .changeColor(dto.color() == null ? model.getColor() : CategoryColor.of(dto.color()));

        return this.updateCategoryAdapter.update(model);
    }

    @Override
    public CategoryModel create(CreateCategoryDtoService dto) {
        log.info("Creating category '" + dto.name() + "' for user: " + dto.userId());
        this.validateUserExists(dto.userId());
        var model = CategoryMapperService.toCategoryModel(dto);
        return this.createCategoryAdapter.create(model);
    }

    private void validateUserExists(UUID userId) {
        log.info("Validating existence of user: " + userId);
        this.findUserUseCase.findById(userId);
    }

}
