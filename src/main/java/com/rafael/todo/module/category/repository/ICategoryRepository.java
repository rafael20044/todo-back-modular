package com.rafael.todo.module.category.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rafael.todo.module.category.repository.entity.CategoryEntity;

public interface ICategoryRepository extends JpaRepository<CategoryEntity, Long>{

    @Query ("SELECT c FROM CategoryEntity c WHERE c.userId = :userId")
    List<CategoryEntity> findAllByUserId(UUID userId);

}
