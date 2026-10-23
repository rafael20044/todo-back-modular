package com.rafael.todo.module.user.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rafael.todo.module.user.controller.dto.CreateUserDto;
import com.rafael.todo.module.user.controller.dto.ResponseUserDto;
import com.rafael.todo.module.user.controller.dto.UpdateUserDto;
import com.rafael.todo.module.user.controller.mapper.UserMapperController;
import com.rafael.todo.module.user.service.ICreateUserUseCase;
import com.rafael.todo.module.user.service.IDeleteUserUseCase;
import com.rafael.todo.module.user.service.IFindUserUseCase;
import com.rafael.todo.module.user.service.IUpdateUserUseCase;

@RestController 
@RequestMapping("/users")
public class UserController {

    private final ICreateUserUseCase createUser;
    private final IFindUserUseCase findUser;
    private final IUpdateUserUseCase updateUser;
    private final IDeleteUserUseCase deleteUser;

    @Autowired 
    public UserController(ICreateUserUseCase createUser, IFindUserUseCase findUser, IUpdateUserUseCase updateUser, IDeleteUserUseCase deleteUser) {
        this.createUser = createUser;
        this.findUser = findUser;
        this.updateUser = updateUser;
        this.deleteUser = deleteUser;
    }
    
    @PostMapping 
    public ResponseEntity<ResponseUserDto> create(@RequestBody CreateUserDto dto){
        var model = this.createUser.create(UserMapperController.toServiceDto(dto));
        var response = UserMapperController.toResponseDto(model);
        var uri = URI.create("/users/" + response.id());
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ResponseUserDto>> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ){
        var users = this.findUser.findAll();
        var pages = UserMapperController.toPage(users, page, size);
        return ResponseEntity.ok(pages);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ResponseUserDto> findById(@PathVariable UUID uuid){
        var user = this.findUser.findById(uuid);
        return ResponseEntity.ok(UserMapperController.toResponseDto(user));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ResponseUserDto> update(@PathVariable UUID uuid, @RequestBody UpdateUserDto dto){
        var model = this.updateUser.update(UserMapperController.toServiceDto(uuid, dto));
        var response = UserMapperController.toResponseDto(model);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping ("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid){
        this.deleteUser.delete(uuid);
        return ResponseEntity.noContent().build();
    }

}
