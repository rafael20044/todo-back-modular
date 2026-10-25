package com.rafael.todo.module.category.service;

import java.util.List;
import java.util.UUID;

import com.rafael.todo.module.category.model.CategoryModel;

public interface IFindCategoryUseCase {

    CategoryModel findById(Long id);

    List<CategoryModel> findAll(UUID userId);

}
