package com.rafael.todo.module.user.service.imp;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rafael.todo.module.user.exception.UserEmailException;
import com.rafael.todo.module.user.exception.UserUuidException;
import com.rafael.todo.module.user.model.UserModel;
import com.rafael.todo.module.user.model.vo.UserName;
import com.rafael.todo.module.user.model.vo.UserPassword;
import com.rafael.todo.module.user.model.vo.UserRole;
import com.rafael.todo.module.user.repository.adapter.ICreateUserAdapter;
import com.rafael.todo.module.user.repository.adapter.IDeleteUserAdapter;
import com.rafael.todo.module.user.repository.adapter.IFindUserAdapter;
import com.rafael.todo.module.user.repository.adapter.IUpdateUserAdapter;
import com.rafael.todo.module.user.service.IDeleteUserUseCase;
import com.rafael.todo.module.user.service.IFindUserUseCase;
import com.rafael.todo.module.user.service.IUpdateUserUseCase;
import com.rafael.todo.module.user.service.ICreateUserUseCase;
import com.rafael.todo.module.user.service.dto.CreateUserDtoService;
import com.rafael.todo.module.user.service.dto.UpdateUserDtoService;
import com.rafael.todo.module.user.service.mapper.UserMapperService;

import lombok.extern.java.Log;

@Service 
@Log
public final class UserService implements 
    ICreateUserUseCase, 
    IFindUserUseCase, 
    IDeleteUserUseCase, 
    IUpdateUserUseCase 
{

    private final ICreateUserAdapter createUserAdapter;
    private final IUpdateUserAdapter updateUserAdapter;
    private final IDeleteUserAdapter deleteUserAdapter;
    private final IFindUserAdapter findUserAdapter;

    @Autowired 
    public UserService(
        ICreateUserAdapter createUserAdapter,
        IUpdateUserAdapter updateUserAdapter,
        IDeleteUserAdapter deleteUserAdapter,
        IFindUserAdapter findUserAdapter
    ) {
        this.createUserAdapter = createUserAdapter;
        this.updateUserAdapter = updateUserAdapter;
        this.deleteUserAdapter = deleteUserAdapter;
        this.findUserAdapter = findUserAdapter;
    }

    @Override
    public UserModel update(UpdateUserDtoService user) {
        log.info("Updating user with UUID: " + user.uuid());
        var model = findById(user.uuid());
        var update = user.password() != null;
        model = model
            .changeName(user.name() == null ? model.getName() : UserName.of(user.name()))
            .changePassword(user.password() == null ? model.getPassword() : UserPassword.fromRaw(user.password()))
            .changeRole(user.role() == null ? model.getRole() : UserRole.of(user.role()));
        
        return this.updateUserAdapter.update(model, update);
    }

    @Override
    public void delete(UUID uuid) {
        log.info("Deleting user with UUID: " + uuid);
        this.deleteUserAdapter.delete(uuid);
    }

    @Override
    public UserModel findById(UUID uuid) {
        var exist = this.findUserAdapter.findById(uuid);
        if (exist.isEmpty()) {
            log.warning("User not found with UUID: " + uuid);
            throw UserUuidException.notFound();
        }
        return exist.get();
    }

    @Override
    public List<UserModel> findAll() {
        log.info("Fetching all users from persistence");
        return this.findUserAdapter.findAll();
    }

    @Override
    public UserModel create(CreateUserDtoService userDto) {
        log.info("Validating email uniqueness for: " + userDto.email());
        var exists = this.findUserAdapter.findByEmail(userDto.email());

        if (exists.isPresent()){
            log.warning("Email already registered: " + userDto.email());
            throw UserEmailException.alreadyExists();
        }

        var model = UserMapperService.toUserModel(userDto);
        return this.createUserAdapter.create(model);
    }

}
