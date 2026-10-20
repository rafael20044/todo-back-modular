package com.rafael.todo.module.user.service.imp;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.rafael.todo.module.user.model.UserModel;
import com.rafael.todo.module.user.service.IDeleteUserUseCase;
import com.rafael.todo.module.user.service.IFindUserUseCase;
import com.rafael.todo.module.user.service.IUpdateUserUseCase;
import com.rafael.todo.module.user.service.IUserCreateUseCase;
import com.rafael.todo.module.user.service.dto.CreateUserDtoService;
import com.rafael.todo.module.user.service.dto.UpdateUserDtoService;

@Service 
public final class UserService implements 
    IUserCreateUseCase, 
    IFindUserUseCase, 
    IDeleteUserUseCase, 
    IUpdateUserUseCase 
    {

    @Override
    public UserModel update(UpdateUserDtoService user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(UUID uuid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public UserModel findById(UUID uuid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public List<UserModel> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public UserModel create(CreateUserDtoService userDto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

}
