package com.rafael.todo.module.auth.service;

import com.rafael.todo.module.auth.service.dto.AuthTokensDtoService;
import com.rafael.todo.module.auth.service.dto.LoginDtoService;

public interface ILoginUseCase {

    AuthTokensDtoService login(LoginDtoService dto);

}

