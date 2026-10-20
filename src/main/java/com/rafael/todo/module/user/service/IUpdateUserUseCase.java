package com.rafael.todo.module.user.service;

import com.rafael.todo.module.user.model.UserModel;
import com.rafael.todo.module.user.service.dto.UpdateUserDtoService;

public interface IUpdateUserUseCase {

    UserModel update(UpdateUserDtoService user);

}
