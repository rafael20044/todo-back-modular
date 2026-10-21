package com.rafael.todo.module.user.service.mapper;

import com.rafael.todo.module.user.model.UserModel;
import com.rafael.todo.module.user.model.vo.UserEmail;
import com.rafael.todo.module.user.model.vo.UserName;
import com.rafael.todo.module.user.model.vo.UserPassword;
import com.rafael.todo.module.user.model.vo.UserRole;
import com.rafael.todo.module.user.service.dto.CreateUserDtoService;

public final class UserMapperService {

    public UserMapperService(){}

    public static  UserModel toUserModel(CreateUserDtoService createDTO) {
        return createDTO.role() == null ? 
            UserModel.create(
                UserName.of(createDTO.name()), 
                UserEmail.of(createDTO.email()), 
                UserPassword.fromRaw(createDTO.password())) 
            : UserModel.create(
                UserName.of(createDTO.name()), 
                UserEmail.of(createDTO.email()), 
                UserPassword.fromRaw(createDTO.password()),
                UserRole.of(createDTO.role()));
    }

}
