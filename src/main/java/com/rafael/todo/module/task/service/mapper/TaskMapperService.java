package com.rafael.todo.module.task.service.mapper;

import com.rafael.todo.module.task.model.TaskModel;
import com.rafael.todo.module.task.model.vo.TaskCategoryId;
import com.rafael.todo.module.task.model.vo.TaskDescription;
import com.rafael.todo.module.task.model.vo.TaskDueDate;
import com.rafael.todo.module.task.model.vo.TaskPriority;
import com.rafael.todo.module.task.model.vo.TaskTitle;
import com.rafael.todo.module.task.model.vo.TaskUserId;
import com.rafael.todo.module.task.service.dto.CreateTaskDtoService;

public final class TaskMapperService {

    private TaskMapperService() {}

    public static TaskModel toTaskModel(CreateTaskDtoService dto) {
        return TaskModel.create(
            TaskUserId.of(dto.userId()),
            dto.categoryId() != null ? TaskCategoryId.of(dto.categoryId()) : null,
            TaskTitle.of(dto.title()),
            dto.description() != null ? TaskDescription.of(dto.description()) : TaskDescription.empty(),
            dto.priority() != null ? TaskPriority.of(dto.priority()) : TaskPriority.medium(),
            dto.dueDate() != null ? TaskDueDate.of(dto.dueDate()) : TaskDueDate.empty()
        );
    }

}
