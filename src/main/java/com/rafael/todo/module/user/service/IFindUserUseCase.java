package com.rafael.todo.module.user.service;

import java.util.List;
import java.util.UUID;

import com.rafael.todo.module.user.model.UserModel;

public interface IFindUserUseCase {

    UserModel findById(UUID uuid);

    List<UserModel> findAll();


}
