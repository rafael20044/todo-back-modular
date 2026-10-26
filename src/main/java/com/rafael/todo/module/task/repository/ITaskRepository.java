package com.rafael.todo.module.task.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rafael.todo.module.task.repository.entity.TaskEntity;

public interface ITaskRepository extends JpaRepository<TaskEntity, Long> {

    @Query("""
        SELECT t FROM TaskEntity t
        WHERE (:userId IS NULL OR t.userId = :userId)
          AND (:categoryId IS NULL OR t.categoryId = :categoryId)
          AND (:status IS NULL OR t.status = :status)
    """)
    List<TaskEntity> findAllFiltered(
        @Param("userId") UUID userId,
        @Param("categoryId") Long categoryId,
        @Param("status") String status
    );

}
