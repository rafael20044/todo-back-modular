package com.rafael.todo.module.subtask.repository.mapper;

import com.rafael.todo.module.subtask.model.SubtaskModel;
import com.rafael.todo.module.subtask.model.vo.SubtaskCompleted;
import com.rafael.todo.module.subtask.model.vo.SubtaskId;
import com.rafael.todo.module.subtask.model.vo.SubtaskPosition;
import com.rafael.todo.module.subtask.model.vo.SubtaskTaskId;
import com.rafael.todo.module.subtask.model.vo.SubtaskTitle;
import com.rafael.todo.module.subtask.repository.entity.SubtaskEntity;

public final class SubtaskMapperRepository {

    private SubtaskMapperRepository() {}

    public static SubtaskEntity toEntity(SubtaskModel model) {
        return SubtaskEntity.builder()
            .id(model.getIdValue())
            .taskId(model.getTaskIdValue())
            .title(model.getTitleValue())
            .isCompleted(model.isCompletedValue())
            .position(model.getPositionValue())
            .createdAt(model.getCreatedAt())
            .updatedAt(model.getUpdatedAt())
            .build();
    }

    public static SubtaskModel toModel(SubtaskEntity entity) {
        return SubtaskModel.reconstitute(
            SubtaskId.of(entity.getId()),
            SubtaskTaskId.of(entity.getTaskId()),
            SubtaskTitle.of(entity.getTitle()),
            SubtaskCompleted.of(entity.isCompleted()),
            SubtaskPosition.of(entity.getPosition()),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

}

