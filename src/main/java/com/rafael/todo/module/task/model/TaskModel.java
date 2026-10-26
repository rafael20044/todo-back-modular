package com.rafael.todo.module.task.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.rafael.todo.module.task.model.enums.TaskPriorityEnum;
import com.rafael.todo.module.task.model.enums.TaskStatusEnum;
import com.rafael.todo.module.task.model.vo.TaskCategoryId;
import com.rafael.todo.module.task.model.vo.TaskDescription;
import com.rafael.todo.module.task.model.vo.TaskDueDate;
import com.rafael.todo.module.task.model.vo.TaskId;
import com.rafael.todo.module.task.model.vo.TaskPriority;
import com.rafael.todo.module.task.model.vo.TaskStatus;
import com.rafael.todo.module.task.model.vo.TaskTitle;
import com.rafael.todo.module.task.model.vo.TaskUserId;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class TaskModel {

    private final TaskId id;
    private final TaskUserId userId;
    private final TaskCategoryId categoryId;
    private final TaskTitle title;
    private final TaskDescription description;
    private final TaskStatus status;
    private final TaskPriority priority;
    private final TaskDueDate dueDate;
    private final LocalDateTime completedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static TaskModel create(
            TaskUserId userId,
            TaskCategoryId categoryId,
            TaskTitle title,
            TaskDescription description,
            TaskPriority priority,
            TaskDueDate dueDate
    ) {
        var now = LocalDateTime.now();
        return new TaskModel(
            null,
            userId,
            categoryId,
            title,
            description != null ? description : TaskDescription.empty(),
            TaskStatus.pending(),
            priority != null ? priority : TaskPriority.medium(),
            dueDate != null ? dueDate : TaskDueDate.empty(),
            null,
            now,
            now
        );
    }

    public static TaskModel create(TaskUserId userId, TaskTitle title) {
        return create(userId, null, title, TaskDescription.empty(), TaskPriority.medium(), TaskDueDate.empty());
    }

    public static TaskModel reconstitute(
            TaskId id,
            TaskUserId userId,
            TaskCategoryId categoryId,
            TaskTitle title,
            TaskDescription description,
            TaskStatus status,
            TaskPriority priority,
            TaskDueDate dueDate,
            LocalDateTime completedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new TaskModel(
            id,
            userId,
            categoryId,
            title,
            description,
            status,
            priority,
            dueDate,
            completedAt,
            createdAt,
            updatedAt
        );
    }

    public TaskModel changeTitle(TaskTitle newTitle) {
        return copy(newTitle, this.description, this.categoryId, this.status, this.priority, this.dueDate, this.completedAt);
    }

    public TaskModel changeDescription(TaskDescription newDescription) {
        return copy(this.title, newDescription, this.categoryId, this.status, this.priority, this.dueDate, this.completedAt);
    }

    public TaskModel changeCategory(TaskCategoryId newCategoryId) {
        return copy(this.title, this.description, newCategoryId, this.status, this.priority, this.dueDate, this.completedAt);
    }

    public TaskModel changePriority(TaskPriority newPriority) {
        return copy(this.title, this.description, this.categoryId, this.status, newPriority, this.dueDate, this.completedAt);
    }

    public TaskModel changeDueDate(TaskDueDate newDueDate) {
        return copy(this.title, this.description, this.categoryId, this.status, this.priority, newDueDate, this.completedAt);
    }

    public TaskModel markCompleted() {
        return copy(this.title, this.description, this.categoryId, TaskStatus.completed(), this.priority, this.dueDate, LocalDateTime.now());
    }

    public TaskModel markPending() {
        return copy(this.title, this.description, this.categoryId, TaskStatus.pending(), this.priority, this.dueDate, null);
    }

    public TaskModel changeStatus(TaskStatus newStatus) {
        var completed = (newStatus != null && newStatus.value() == TaskStatusEnum.COMPLETED)
                ? (this.completedAt != null ? this.completedAt : LocalDateTime.now())
                : null;
        return copy(this.title, this.description, this.categoryId, newStatus, this.priority, this.dueDate, completed);
    }

    public Long getIdValue() {
        return id != null ? id.value() : null;
    }

    public UUID getUserIdValue() {
        return userId.value();
    }

    public Long getCategoryIdValue() {
        return categoryId != null ? categoryId.value() : null;
    }

    public String getTitleValue() {
        return title.value();
    }

    public String getDescriptionValue() {
        return description != null ? description.value() : null;
    }

    public TaskStatusEnum getStatusValue() {
        return status.value();
    }

    public TaskPriorityEnum getPriorityValue() {
        return priority.value();
    }

    public LocalDateTime getDueDateValue() {
        return dueDate != null ? dueDate.value() : null;
    }

    private TaskModel copy(
            TaskTitle title,
            TaskDescription description,
            TaskCategoryId categoryId,
            TaskStatus status,
            TaskPriority priority,
            TaskDueDate dueDate,
            LocalDateTime completedAt
    ) {
        return new TaskModel(
            this.id,
            this.userId,
            categoryId,
            title,
            description,
            status,
            priority,
            dueDate,
            completedAt,
            this.createdAt,
            LocalDateTime.now()
        );
    }

}
