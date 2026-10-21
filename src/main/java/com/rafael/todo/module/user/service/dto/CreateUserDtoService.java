package com.rafael.todo.module.user.service.dto;

public record CreateUserDtoService(
        String name,
        String email,
        String password,
        String role
    ) {
}
