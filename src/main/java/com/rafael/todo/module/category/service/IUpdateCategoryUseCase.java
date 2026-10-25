package com.rafael.todo.module.category.service;

import com.rafael.todo.module.category.model.CategoryModel;
import com.rafael.todo.module.category.service.dto.UpdateCategoryDtoService;

public interface IUpdateCategoryUseCase {

    CategoryModel update(UpdateCategoryDtoService dto);

}
