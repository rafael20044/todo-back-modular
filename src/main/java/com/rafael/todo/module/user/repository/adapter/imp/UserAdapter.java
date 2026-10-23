package com.rafael.todo.module.user.repository.adapter.imp;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.rafael.todo.module.user.model.UserModel;
import com.rafael.todo.module.user.repository.IUserRepository;
import com.rafael.todo.module.user.repository.adapter.ICreateUserAdapter;
import com.rafael.todo.module.user.repository.adapter.IDeleteUserAdapter;
import com.rafael.todo.module.user.repository.adapter.IFindUserAdapter;
import com.rafael.todo.module.user.repository.adapter.IUpdateUserAdapter;
import com.rafael.todo.module.user.repository.mapper.UserMapperRepository;

import lombok.extern.java.Log;

@Component 
@Log
public final class UserAdapter implements 
    ICreateUserAdapter, 
    IDeleteUserAdapter, 
    IFindUserAdapter, 
    IUpdateUserAdapter 
{

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired 
    public UserAdapter(
        IUserRepository userRepository, 
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserModel create(UserModel model) {
        log.info("Persisting new user in database with email: " + model.getEmailValue());
        var entity = UserMapperRepository.toEntity(model);
        entity.setPasswordHash(passwordEncoder.encode(entity.getPasswordHash()));
        var save = this.userRepository.save(entity);
        log.info("User persisted successfully with ID: " + save.getId());
        return UserMapperRepository.toModel(save);
    }

    @Override
    public void delete(UUID uuid) {
        log.info("Deleting user entity from database with ID: " + uuid);
        this.userRepository.deleteById(uuid);
    }

    @Override
    public Optional<UserModel> findById(UUID uuid) {
        log.info("Querying user by ID: " + uuid);
        var exist = this.userRepository.findById(uuid);
        return exist.map(UserMapperRepository::toModel);
    }

    @Override
    public List<UserModel> findAll() {
        log.info("Querying all users from database");
        return this.userRepository.findAll().stream().map(UserMapperRepository::toModel).toList();
    }

    @Override
    public Optional<UserModel> findByEmail(String email) {
        log.info("Querying user by email: " + email);
        var exist = this.userRepository.findByEmail(email);
        return exist.map(UserMapperRepository::toModel);
    }

    @Override
    public UserModel update(UserModel user, boolean updatePassword) {
        log.info("Updating user entity in database: " + user.getIdValue() + " (password updated: " + updatePassword + ")");
        var entity = UserMapperRepository.toEntity(user);
        
        if (updatePassword) {
            entity.setPasswordHash(passwordEncoder.encode(entity.getPasswordHash()));
        }

        var saved = this.userRepository.save(entity);
        log.info("User entity updated successfully in database: " + saved.getId());
        return UserMapperRepository.toModel(saved);
    }

}
