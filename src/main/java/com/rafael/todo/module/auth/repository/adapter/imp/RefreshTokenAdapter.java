package com.rafael.todo.module.auth.repository.adapter.imp;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rafael.todo.module.auth.model.RefreshTokenModel;
import com.rafael.todo.module.auth.repository.IRefreshTokenRepository;
import com.rafael.todo.module.auth.repository.adapter.ICreateRefreshTokenAdapter;
import com.rafael.todo.module.auth.repository.adapter.IDeleteRefreshTokenAdapter;
import com.rafael.todo.module.auth.repository.adapter.IFindRefreshTokenAdapter;
import com.rafael.todo.module.auth.repository.adapter.IUpdateRefreshTokenAdapter;
import com.rafael.todo.module.auth.repository.mapper.RefreshTokenMapperRepository;

import lombok.extern.java.Log;

@Component
@Log
public final class RefreshTokenAdapter implements
        ICreateRefreshTokenAdapter,
        IFindRefreshTokenAdapter,
        IUpdateRefreshTokenAdapter,
        IDeleteRefreshTokenAdapter {

    private final IRefreshTokenRepository repository;

    @Autowired
    public RefreshTokenAdapter(IRefreshTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public RefreshTokenModel create(RefreshTokenModel refreshToken) {
        return this.saveEntity(refreshToken);
    }

    @Override
    public Optional<RefreshTokenModel> findById(Long id) {
        return this.repository.findById(id).map(RefreshTokenMapperRepository::toModel);
    }

    @Override
    public Optional<RefreshTokenModel> findByTokenHash(String tokenHash) {
        return this.repository.findByTokenHash(tokenHash).map(RefreshTokenMapperRepository::toModel);
    }

    @Override
    public List<RefreshTokenModel> findAllByUserId(UUID userId) {
        var list = this.repository.findAllByUserId(userId);
        return list.stream().map(RefreshTokenMapperRepository::toModel).toList();
    }

    @Override
    public List<RefreshTokenModel> findAllFiltered(UUID userId, Boolean isRevoked) {
        var list = this.repository.findAllFiltered(userId, isRevoked);
        return list.stream().map(RefreshTokenMapperRepository::toModel).toList();
    }

    @Override
    public RefreshTokenModel update(RefreshTokenModel refreshToken) {
        return this.saveEntity(refreshToken);
    }

    @Override
    public void delete(Long id) {
        this.repository.deleteById(id);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        this.repository.deleteByUserId(userId);
    }

    private RefreshTokenModel saveEntity(RefreshTokenModel refreshToken) {
        var entity = RefreshTokenMapperRepository.toEntity(refreshToken);
        return RefreshTokenMapperRepository.toModel(this.repository.save(entity));
    }

}
