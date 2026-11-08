package com.rafael.todo.module.auth.repository.adapter;

import com.rafael.todo.module.auth.model.RefreshTokenModel;

public interface IUpdateRefreshTokenAdapter {

    RefreshTokenModel update(RefreshTokenModel refreshToken);

}

