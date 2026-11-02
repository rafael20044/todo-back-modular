package com.rafael.todo.module.subtask.model;

import java.time.LocalDateTime;

import com.rafael.todo.module.subtask.model.vo.SubtaskCompleted;
import com.rafael.todo.module.subtask.model.vo.SubtaskId;
import com.rafael.todo.module.subtask.model.vo.SubtaskPosition;
import com.rafael.todo.module.subtask.model.vo.SubtaskTaskId;
import com.rafael.todo.module.subtask.model.vo.SubtaskTitle;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SubtaskModel {

    private final SubtaskId id;
    private final SubtaskTaskId taskId;
    private final SubtaskTitle title;
    private final SubtaskCompleted isCompleted;
    private final SubtaskPosition position;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static SubtaskModel create(SubtaskTaskId taskId, SubtaskTitle title, SubtaskPosition position) {
        var now = LocalDateTime.now();
        return new SubtaskModel(
            null,
            taskId,
            title,
            SubtaskCompleted.pending(),
            position != null ? position : SubtaskPosition.defaultPosition(),
            now,
            now
        );
    }

    public static SubtaskModel create(SubtaskTaskId taskId, SubtaskTitle title) {
        return create(taskId, title, SubtaskPosition.defaultPosition());
    }

    public static SubtaskModel reconstitute(
            SubtaskId id,
            SubtaskTaskId taskId,
            SubtaskTitle title,
            SubtaskCompleted isCompleted,
            SubtaskPosition position,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new SubtaskModel(id, taskId, title, isCompleted, position, createdAt, updatedAt);
    }

    public SubtaskModel changeTitle(SubtaskTitle newTitle) {
        return copy(newTitle, this.isCompleted, this.position);
    }

    public SubtaskModel changePosition(SubtaskPosition newPosition) {
        return copy(this.title, this.isCompleted, newPosition);
    }

    public SubtaskModel markCompleted() {
        return copy(this.title, SubtaskCompleted.completed(), this.position);
    }

    public SubtaskModel markPending() {
        return copy(this.title, SubtaskCompleted.pending(), this.position);
    }

    public SubtaskModel toggleCompleted() {
        return copy(this.title, SubtaskCompleted.of(!this.isCompleted.value()), this.position);
    }

    public Long getIdValue() {
        return id != null ? id.value() : null;
    }

    public Long getTaskIdValue() {
        return taskId.value();
    }

    public String getTitleValue() {
        return title.value();
    }

    public boolean isCompletedValue() {
        return isCompleted.value();
    }

    public Integer getPositionValue() {
        return position.value();
    }

    private SubtaskModel copy(SubtaskTitle title, SubtaskCompleted isCompleted, SubtaskPosition position) {
        return new SubtaskModel(
            this.id,
            this.taskId,
            title,
            isCompleted,
            position,
            this.createdAt,
            LocalDateTime.now()
        );
    }

}

