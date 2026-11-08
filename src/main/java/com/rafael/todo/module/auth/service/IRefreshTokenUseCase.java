package com.rafael.todo.module.auth.service;

import com.rafael.todo.module.auth.service.dto.AuthTokensDtoService;
import com.rafael.todo.module.auth.service.dto.RefreshTokenRequestDtoService;

public interface IRefreshTokenUseCase {

    AuthTokensDtoService refresh(RefreshTokenRequestDtoService dto);

}

