package com.rafael.todo.module.user.controller.mapper;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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

    public static Page<ResponseUserDto> toPage(List<UserModel> users, int page, int size){
        var pageable = PageRequest.of(page, size);
        var pages = new PageImpl<>(users, pageable, users.size());
        return pages.map(UserMapperController::toResponseDto);
    }

}
