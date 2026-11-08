package com.rafael.todo.module.task.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.rafael.todo.module.task.exception.TaskIdException;
import com.rafael.todo.module.task.exception.TaskTitleException;
import com.rafael.todo.module.task.model.TaskModel;
import com.rafael.todo.module.task.model.enums.TaskStatusEnum;
import com.rafael.todo.module.task.model.vo.TaskCategoryId;
import com.rafael.todo.module.task.model.vo.TaskDescription;
import com.rafael.todo.module.task.model.vo.TaskDueDate;
import com.rafael.todo.module.task.model.vo.TaskId;
import com.rafael.todo.module.task.model.vo.TaskPriority;
import com.rafael.todo.module.task.model.vo.TaskStatus;
import com.rafael.todo.module.task.model.vo.TaskTitle;
import com.rafael.todo.module.task.model.vo.TaskUserId;
import com.rafael.todo.module.task.service.ICreateTaskUseCase;
import com.rafael.todo.module.task.service.IDeleteTaskUseCase;
import com.rafael.todo.module.task.service.IFindTaskUseCase;
import com.rafael.todo.module.task.service.IUpdateTaskUseCase;
import com.rafael.todo.module.task.service.dto.CreateTaskDtoService;
import com.rafael.todo.module.task.service.dto.UpdateTaskDtoService;
import com.rafael.todo.shared.GlobalExceptionController;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskController - REST Endpoints & Exception Handling Tests")
class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ICreateTaskUseCase createTaskUseCase;

    @Mock
    private IFindTaskUseCase findTaskUseCase;

    @Mock
    private IUpdateTaskUseCase updateTaskUseCase;

    @Mock
    private IDeleteTaskUseCase deleteTaskUseCase;

    @InjectMocks
    private TaskController taskController;

    private UUID sampleUserId;
    private TaskModel sampleTask;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(new GlobalExceptionController())
                .build();

        sampleUserId = UUID.randomUUID();
        LocalDateTime fixedTime = LocalDateTime.of(2026, 11, 10, 12, 0);

        sampleTask = TaskModel.reconstitute(
                TaskId.of(10L),
                TaskUserId.of(sampleUserId),
                TaskCategoryId.of(1L),
                TaskTitle.of("Preparar presentación"),
                TaskDescription.of("Incluir métricas del Q4"),
                TaskStatus.pending(),
                TaskPriority.high(),
                TaskDueDate.of(LocalDateTime.of(2026, 11, 15, 18, 0)),
                null,
                fixedTime,
                fixedTime
        );
    }

    @Test
    @DisplayName("POST /tasks - Should return 201 Created with Location header and ResponseTaskDto")
    void shouldCreateTaskAndReturn201() throws Exception {
        when(createTaskUseCase.create(any(CreateTaskDtoService.class))).thenReturn(sampleTask);

        String jsonBody = String.format("""
                {
                  "userId": "%s",
                  "categoryId": 1,
                  "title": "Preparar presentación",
                  "description": "Incluir métricas del Q4",
                  "priority": "HIGH",
                  "dueDate": "2026-11-15T18:00:00"
                }
                """, sampleUserId);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/tasks/10"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.userId").value(sampleUserId.toString()))
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.title").value("Preparar presentación"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    @DisplayName("POST /tasks - Should return 400 Bad Request when task title is invalid")
    void shouldReturn400WhenTitleIsInvalid() throws Exception {
        when(createTaskUseCase.create(any(CreateTaskDtoService.class)))
                .thenThrow(TaskTitleException.tooShort());

        String jsonBody = String.format("""
                {
                  "userId": "%s",
                  "title": "A"
                }
                """, sampleUserId);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("task.title.too.short"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("GET /tasks - Should return 200 OK with paginated tasks filtered by userId, categoryId and status")
    void shouldListTasksWithFilters() throws Exception {
        when(findTaskUseCase.findAll(sampleUserId, 1L, TaskStatusEnum.PENDING))
                .thenReturn(List.of(sampleTask));

        mockMvc.perform(get("/tasks")
                        .param("userId", sampleUserId.toString())
                        .param("categoryId", "1")
                        .param("status", "PENDING")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].title").value("Preparar presentación"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /tasks/{id} - Should return 200 OK when task exists")
    void shouldFindTaskById() throws Exception {
        when(findTaskUseCase.findById(10L)).thenReturn(sampleTask);

        mockMvc.perform(get("/tasks/{id}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Preparar presentación"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /tasks/{id} - Should return 404 Not Found when task does not exist")
    void shouldReturn404WhenTaskNotFound() throws Exception {
        when(findTaskUseCase.findById(999L)).thenThrow(TaskIdException.notFound());

        mockMvc.perform(get("/tasks/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("task.id.not.found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("PUT /tasks/{id} - Should return 200 OK with updated ResponseTaskDto")
    void shouldUpdateTaskAndReturn200() throws Exception {
        TaskModel completedTask = sampleTask
                .changeTitle(TaskTitle.of("Presentación Finalizada"))
                .markCompleted();

        when(updateTaskUseCase.update(any(UpdateTaskDtoService.class))).thenReturn(completedTask);

        String jsonBody = """
                {
                  "categoryId": 1,
                  "title": "Presentación Finalizada",
                  "description": "Incluir métricas del Q4",
                  "status": "COMPLETED",
                  "priority": "HIGH",
                  "dueDate": "2026-11-15T18:00:00"
                }
                """;

        mockMvc.perform(put("/tasks/{id}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Presentación Finalizada"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("DELETE /tasks/{id} - Should return 204 No Content")
    void shouldDeleteTaskAndReturn204() throws Exception {
        doNothing().when(deleteTaskUseCase).delete(10L);

        mockMvc.perform(delete("/tasks/{id}", 10L))
                .andExpect(status().isNoContent());

        verify(deleteTaskUseCase).delete(10L);
    }
}

