package com.rafael.todo.module.user.repository.mapper;

import com.rafael.todo.module.user.model.UserModel;
import com.rafael.todo.module.user.model.vo.UserAccount;
import com.rafael.todo.module.user.model.vo.UserEmail;
import com.rafael.todo.module.user.model.vo.UserId;
import com.rafael.todo.module.user.model.vo.UserName;
import com.rafael.todo.module.user.model.vo.UserPassword;
import com.rafael.todo.module.user.model.vo.UserRole;
import com.rafael.todo.module.user.repository.entity.UserEntity;

public final class UserMapperRepository {

    public UserMapperRepository() {}

    public static UserEntity toEntity(UserModel model){
        return UserEntity.builder()
            .id(model.getIdValue())
            .name(model.getNameValue())
            .email(model.getEmailValue())
            .passwordHash(model.getPasswordValue())
            .role(model.getRoleValue().getValue())
            .isActive(model.getAccountValue())
            .createdAt(model.getCreatedAt())
            .updatedAt(model.getUpdatedAt())
            .build();
    }

    public static UserModel toModel(UserEntity entity){
        return UserModel.reconstitute(
            UserId.of(entity.getId()), 
            UserEmail.of(entity.getEmail()), 
            UserPassword.fromHash(entity.getPasswordHash()), 
            UserName.of(entity.getName()), 
            UserRole.of(entity.getRole()), 
            UserAccount.of(entity.isActive()), 
            entity.getCreatedAt(), 
            entity.getUpdatedAt());
    }

    
    
}
