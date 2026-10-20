package com.rafael.todo.module.user.service;

import com.rafael.todo.module.user.model.UserModel;
import com.rafael.todo.module.user.service.dto.CreateUserDtoService;

public interface ICreateUserUseCase {

    UserModel create(CreateUserDtoService userDto);

}
