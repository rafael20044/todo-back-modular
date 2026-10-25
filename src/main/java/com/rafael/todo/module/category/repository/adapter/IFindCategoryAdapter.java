package com.rafael.todo.module.category.repository.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.rafael.todo.module.category.model.CategoryModel;

public interface IFindCategoryAdapter {

    Optional<CategoryModel> findById(Long id);

    List<CategoryModel> findAll(UUID userId);

    Optional<CategoryModel> findByUserIdAndName(UUID userId, String name);

}
