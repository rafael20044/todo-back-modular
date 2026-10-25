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


@Component 
public final class CategoryAdapter implements 
    ICreateCategoryAdapter, 
    IUpdateCategoryAdapter, 
    IDeleteCategoryAdapter, 
    IFindCategoryAdapter{

    private final ICategoryRepository repository;

    @Autowired 
    public CategoryAdapter(ICategoryRepository repository) {
        this.repository = repository;
    } 


    @Override
    public Optional<CategoryModel> findById(Long id) {
        return repository.findById(id).map(CategoryMapperRepository::toModel);
    }

    @Override
    public List<CategoryModel> findAll(UUID userId) {
        return userId == null ? repository.findAll().stream().map(CategoryMapperRepository::toModel).toList()
                : repository.findAllByUserId(userId).stream().map(CategoryMapperRepository::toModel).toList();
    }

    @Override
    public Optional<CategoryModel> findByUserIdAndName(UUID userId, String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUserIdAndName'");
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public CategoryModel update(CategoryModel category) {
        var entity = CategoryMapperRepository.toEntity(category);
        return CategoryMapperRepository.toModel(repository.save(entity));
    }

    @Override
    public CategoryModel create(CategoryModel category) {
        var entity = CategoryMapperRepository.toEntity(category);
        return CategoryMapperRepository.toModel(repository.save(entity));
    }

}
