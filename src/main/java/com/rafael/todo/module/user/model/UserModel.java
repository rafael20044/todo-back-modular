package com.rafael.todo.module.user.model;

import java.time.LocalDateTime;

import com.rafael.todo.module.user.model.vo.UserAccount;
import com.rafael.todo.module.user.model.vo.UserEmail;
import com.rafael.todo.module.user.model.vo.UserId;
import com.rafael.todo.module.user.model.vo.UserName;
import com.rafael.todo.module.user.model.vo.UserPassword;
import com.rafael.todo.module.user.model.vo.UserRole;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserModel {

    private final UserId id;
    private final UserEmail email;
    private final UserPassword password;
    private final UserName name;
    private final UserRole role;
    private final UserAccount account;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static UserModel create(UserName name, UserEmail email, UserPassword password) {
        LocalDateTime now = LocalDateTime.now();
        return new UserModel(
                UserId.random(),
                email,
                password,
                name,
                UserRole.user(),
                UserAccount.active(),
                now,
                now
        );
    }

    public static UserModel create(UserName name, UserEmail email, UserPassword password, UserRole role) {
        LocalDateTime now = LocalDateTime.now();
        return new UserModel(
                UserId.random(),
                email,
                password,
                name,
                role,
                UserAccount.active(),
                now,
                now
        );
    }

    public static UserModel reconstitute(
            UserId id,
            UserEmail email,
            UserPassword password,
            UserName name,
            UserRole role,
            UserAccount account,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new UserModel(id, email, password, name, role, account, createdAt, updatedAt);
    }

    public UserModel updateName(UserName newName) {
        return copy(newName, this.email, this.password, this.role, this.account);
    }

    public UserModel changePassword(UserPassword newPassword) {
        return copy(this.name, this.email, newPassword, this.role, this.account);
    }

    public UserModel changeRole(UserRole newRole) {
        return copy(this.name, this.email, this.password, newRole, this.account);
    }

    public UserModel activate() {
        return copy(this.name, this.email, this.password, this.role, this.account.activate());
    }

    public UserModel deactivate() {
        return copy(this.name, this.email, this.password, this.role, this.account.deactivate());
    }

    private UserModel copy(UserName name, UserEmail email, UserPassword password, UserRole role, UserAccount account){
        return new UserModel(
            this.id, email, password, name, role, account, this.createdAt, LocalDateTime.now()
        );
    }

}
