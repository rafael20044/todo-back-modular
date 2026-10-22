package com.rafael.todo.module.user.controller.mapper;

import com.rafael.todo.module.user.controller.dto.CreateUserDto;
import com.rafael.todo.module.user.controller.dto.ResponseUserDto;
import com.rafael.todo.module.user.model.UserModel;
import com.rafael.todo.module.user.service.dto.CreateUserDtoService;

public final class UserMapperController {

    public UserMapperController(){}

    public static CreateUserDtoService toServiceDto(CreateUserDto dto){
        return new CreateUserDtoService(dto.username(), dto.email(), dto.password(), dto.role());
    }

    public static ResponseUserDto toResponseDto(UserModel model){
        return new ResponseUserDto(
            model.getIdValue(),
            model.getNameValue(),
            model.getEmailValue(),
            model.getRoleValue().getValue(),
            model.getCreatedAt(),
            model.getUpdatedAt()
        );
    }

}
