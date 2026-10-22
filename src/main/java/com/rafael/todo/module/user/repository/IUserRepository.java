package com.rafael.todo.module.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rafael.todo.module.user.repository.entity.UserEntity;

public interface IUserRepository extends JpaRepository<UserEntity, UUID>{    
    Optional<UserEntity> findByEmail(String email);
}
