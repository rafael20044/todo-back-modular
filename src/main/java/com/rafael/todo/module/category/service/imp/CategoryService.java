package com.rafael.todo.module.category.service.imp;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rafael.todo.module.category.model.CategoryModel;
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


@Service 
public class CategoryService implements 
    ICreateCategoryUseCase, 
    IUpdateCategoryUseCase, 
    IFindCategoryUseCase, 
    IDeleteCategoryUseCase{
    
    private final ICreateCategoryAdapter createCategoryAdapter;
    private final IUpdateCategoryAdapter updateCategoryAdapter;
    private final IFindCategoryAdapter findCategoryAdapter;
    private final IDeleteCategoryAdapter deleteCategoryAdapter;

    @Autowired 
    public CategoryService(
        ICreateCategoryAdapter createCategoryAdapter,
        IUpdateCategoryAdapter updateCategoryAdapter,
        IFindCategoryAdapter findCategoryAdapter,
        IDeleteCategoryAdapter deleteCategoryAdapter
    ){
        this.createCategoryAdapter = createCategoryAdapter;
        this.updateCategoryAdapter = updateCategoryAdapter;
        this.findCategoryAdapter = findCategoryAdapter;
        this.deleteCategoryAdapter = deleteCategoryAdapter;
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public CategoryModel findById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public List<CategoryModel> findAll(UUID userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public CategoryModel update(UpdateCategoryDtoService dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public CategoryModel create(CreateCategoryDtoService dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

}
