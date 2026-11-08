package com.rafael.todo.module.auth.repository.adapter;

import java.util.UUID;

public interface IDeleteRefreshTokenAdapter {

    void delete(Long id);

    void deleteByUserId(UUID userId);

}

