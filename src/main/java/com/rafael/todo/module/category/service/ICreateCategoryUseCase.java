package com.rafael.todo.module.category.service;

import com.rafael.todo.module.category.model.CategoryModel;
import com.rafael.todo.module.category.service.dto.CreateCategoryDtoService;

public interface ICreateCategoryUseCase {

    CategoryModel create(CreateCategoryDtoService dto);

}
