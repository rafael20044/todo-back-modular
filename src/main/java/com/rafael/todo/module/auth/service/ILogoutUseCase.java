package com.rafael.todo.module.auth.service;

import java.util.UUID;

public interface ILogoutUseCase {

    void logout(String refreshToken);

    void logoutAllByUserId(UUID userId);

}

