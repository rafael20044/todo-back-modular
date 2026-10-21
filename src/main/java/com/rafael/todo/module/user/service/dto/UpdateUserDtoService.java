package com.rafael.todo.module.user.service.dto;

import java.util.UUID;

public record UpdateUserDtoService(
    UUID uuid, 
    String name, 
    String password, 
    String role
) {

}
