package com.rafael.todo.module.user.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.rafael.todo.module.user.model.UserModel;

public interface IFindUserAdapter {

    Optional<UserModel> findById(UUID id);

    List<UserModel> findAll();

    Optional<UserModel> findByEmail(String email);

}
