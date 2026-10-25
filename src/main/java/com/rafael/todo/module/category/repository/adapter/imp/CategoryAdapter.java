package com.rafael.todo.module.category.repository.adapter.imp;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rafael.todo.module.category.model.CategoryModel;
import com.rafael.todo.module.category.repository.ICategoryRepository;
import com.rafael.todo.module.category.repository.adapter.ICreateCategoryAdapter;
import com.rafael.todo.module.category.repository.adapter.IDeleteCategoryAdapter;
import com.rafael.todo.module.category.repository.adapter.IFindCategoryAdapter;
import com.rafael.todo.module.category.repository.adapter.IUpdateCategoryAdapter;
import com.rafael.todo.module.category.repository.mapper.CategoryMapperRepository;

import lombok.extern.java.Log;

@Component 
@Log
public final class CategoryAdapter implements 
    ICreateCategoryAdapter, 
    IUpdateCategoryAdapter, 
    IDeleteCategoryAdapter, 
    IFindCategoryAdapter {

    private final ICategoryRepository repository;

    @Autowired 
    public CategoryAdapter(ICategoryRepository repository) {
        this.repository = repository;
    } 

    @Override
    public Optional<CategoryModel> findById(Long id) {
        log.info("Querying category by id: " + id);
        return repository.findById(id).map(CategoryMapperRepository::toModel);
    }

    @Override
    public List<CategoryModel> findAll(UUID userId) {
        log.info("Querying categories with filter userId: " + userId);
        return repository.findAllByUserId(userId).stream()
                .map(CategoryMapperRepository::toModel)
                .toList();
    }

    @Override
    public Optional<CategoryModel> findByUserIdAndName(UUID userId, String name) {
        log.info("Querying category by userId: " + userId + " and name: " + name);
        return repository.findByUserIdAndName(userId, name)
                .map(CategoryMapperRepository::toModel);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting category entity by id: " + id);
        repository.deleteById(id);
    }

    @Override
    public CategoryModel update(CategoryModel category) {
        log.info("Updating category entity with id: " + category.getIdValue());
        var entity = CategoryMapperRepository.toEntity(category);
        var saved = repository.save(entity);
        return CategoryMapperRepository.toModel(saved);
    }

    @Override
    public CategoryModel create(CategoryModel category) {
        log.info("Persisting new category entity: " + category.getNameValue());
        var entity = CategoryMapperRepository.toEntity(category);
        var saved = repository.save(entity);
        return CategoryMapperRepository.toModel(saved);
    }

}
