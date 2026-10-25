package com.rafael.todo.module.category.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rafael.todo.module.category.repository.entity.CategoryEntity;

public interface ICategoryRepository extends JpaRepository<CategoryEntity, Long> {

    @Query("SELECT c FROM CategoryEntity c WHERE (:userId IS NULL OR c.userId = :userId)")
    List<CategoryEntity> findAllByUserId(@Param("userId") UUID userId);

    Optional<CategoryEntity> findByUserIdAndName(UUID userId, String name);

}
