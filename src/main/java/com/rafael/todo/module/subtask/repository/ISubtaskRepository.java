package com.rafael.todo.module.subtask.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rafael.todo.module.subtask.repository.entity.SubtaskEntity;

public interface ISubtaskRepository extends JpaRepository<SubtaskEntity, Long> {

    @Query("SELECT s FROM SubtaskEntity s WHERE s.taskId = :taskId ORDER BY s.position ASC, s.createdAt ASC")
    List<SubtaskEntity> findAllByTaskIdOrderByPositionAsc(@Param("taskId") Long taskId);

}

