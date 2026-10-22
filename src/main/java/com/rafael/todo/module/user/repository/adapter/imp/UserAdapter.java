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

@Component 
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
        var entity = UserMapperRepository.toEntity(model);
        entity.setPasswordHash(passwordEncoder.encode(entity.getPasswordHash()));
        var save = this.userRepository.save(entity);
        return UserMapperRepository.toModel(save);
    }

    @Override
    public void delete(UUID uuid) {
        
    }

    @Override
    public UserModel findById(UUID uuid) {
        return null;
    }

    @Override
    public List<UserModel> findAll() {
        return null;
    }

    @Override
    public Optional<UserModel> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public UserModel update(UserModel user) {
        return null;
    }

}
