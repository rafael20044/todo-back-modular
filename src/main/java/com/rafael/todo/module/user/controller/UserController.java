package com.rafael.todo.module.user.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rafael.todo.module.user.controller.dto.CreateUserDto;
import com.rafael.todo.module.user.controller.dto.ResponseUserDto;
import com.rafael.todo.module.user.controller.mapper.UserMapperController;
import com.rafael.todo.module.user.service.ICreateUserUseCase;

@RestController 
@RequestMapping("/users")
public class UserController {

    private final ICreateUserUseCase createUser;

    @Autowired 
    public UserController(ICreateUserUseCase createUser) {
        this.createUser = createUser;
    }
    
    @PostMapping 
    public ResponseEntity<ResponseUserDto> create(@RequestBody CreateUserDto dto){
        var model = this.createUser.create(UserMapperController.toServiceDto(dto));
        var response = UserMapperController.toResponseDto(model);
        var uri = URI.create("/users/" + response.id());
        return ResponseEntity.created(uri).body(response);
    }

}
