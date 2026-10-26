package com.rafael.todo.module.task.repository.mapper;

import com.rafael.todo.module.task.model.TaskModel;
import com.rafael.todo.module.task.model.vo.TaskCategoryId;
import com.rafael.todo.module.task.model.vo.TaskDescription;
import com.rafael.todo.module.task.model.vo.TaskDueDate;
import com.rafael.todo.module.task.model.vo.TaskId;
import com.rafael.todo.module.task.model.vo.TaskPriority;
import com.rafael.todo.module.task.model.vo.TaskStatus;
import com.rafael.todo.module.task.model.vo.TaskTitle;
import com.rafael.todo.module.task.model.vo.TaskUserId;
import com.rafael.todo.module.task.repository.entity.TaskEntity;

public final class TaskMapperRepository {

    private TaskMapperRepository() {}

    public static TaskEntity toEntity(TaskModel model) {
        return TaskEntity.builder()
            .id(model.getIdValue())
            .userId(model.getUserIdValue())
            .categoryId(model.getCategoryIdValue())
            .title(model.getTitleValue())
            .description(model.getDescriptionValue())
            .status(model.getStatusValue().getValue())
            .priority(model.getPriorityValue().getValue())
            .dueDate(model.getDueDateValue())
            .completedAt(model.getCompletedAt())
            .createdAt(model.getCreatedAt())
            .updatedAt(model.getUpdatedAt())
            .build();
    }

    public static TaskModel toModel(TaskEntity entity) {
        return TaskModel.reconstitute(
            TaskId.of(entity.getId()),
            TaskUserId.of(entity.getUserId()),
            entity.getCategoryId() != null ? TaskCategoryId.of(entity.getCategoryId()) : null,
            TaskTitle.of(entity.getTitle()),
            entity.getDescription() != null ? TaskDescription.of(entity.getDescription()) : TaskDescription.empty(),
            TaskStatus.of(entity.getStatus()),
            TaskPriority.of(entity.getPriority()),
            entity.getDueDate() != null ? TaskDueDate.of(entity.getDueDate()) : TaskDueDate.empty(),
            entity.getCompletedAt(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

}
