package com.rafael.todo.module.subtask.controller;

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

import com.rafael.todo.module.subtask.exception.SubtaskIdException;
import com.rafael.todo.module.subtask.exception.SubtaskTitleException;
import com.rafael.todo.module.subtask.model.SubtaskModel;
import com.rafael.todo.module.subtask.model.vo.SubtaskCompleted;
import com.rafael.todo.module.subtask.model.vo.SubtaskId;
import com.rafael.todo.module.subtask.model.vo.SubtaskPosition;
import com.rafael.todo.module.subtask.model.vo.SubtaskTaskId;
import com.rafael.todo.module.subtask.model.vo.SubtaskTitle;
import com.rafael.todo.module.subtask.service.ICreateSubtaskUseCase;
import com.rafael.todo.module.subtask.service.IDeleteSubtaskUseCase;
import com.rafael.todo.module.subtask.service.IFindSubtaskUseCase;
import com.rafael.todo.module.subtask.service.IUpdateSubtaskUseCase;
import com.rafael.todo.module.subtask.service.dto.CreateSubtaskDtoService;
import com.rafael.todo.module.subtask.service.dto.UpdateSubtaskDtoService;
import com.rafael.todo.shared.GlobalExceptionController;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubtaskController - REST Endpoints & Exception Handling Tests")
class SubtaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ICreateSubtaskUseCase createSubtaskUseCase;

    @Mock
    private IFindSubtaskUseCase findSubtaskUseCase;

    @Mock
    private IUpdateSubtaskUseCase updateSubtaskUseCase;

    @Mock
    private IDeleteSubtaskUseCase deleteSubtaskUseCase;

    @InjectMocks
    private SubtaskController subtaskController;

    private SubtaskModel sampleSubtask;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subtaskController)
                .setControllerAdvice(new GlobalExceptionController())
                .build();

        LocalDateTime fixedTime = LocalDateTime.of(2026, 11, 10, 12, 0);
        sampleSubtask = SubtaskModel.reconstitute(
                SubtaskId.of(100L),
                SubtaskTaskId.of(10L),
                SubtaskTitle.of("Exportar gráficos de ventas"),
                SubtaskCompleted.pending(),
                SubtaskPosition.of(1),
                fixedTime,
                fixedTime
        );
    }

    @Test
    @DisplayName("POST /subtasks - Should return 201 Created with Location header and ResponseSubtaskDto")
    void shouldCreateSubtaskAndReturn201() throws Exception {
        when(createSubtaskUseCase.create(any(CreateSubtaskDtoService.class))).thenReturn(sampleSubtask);

        String jsonBody = """
                {
                  "taskId": 10,
                  "title": "Exportar gráficos de ventas",
                  "position": 1
                }
                """;

        mockMvc.perform(post("/subtasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "subtasks/100"))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.taskId").value(10))
                .andExpect(jsonPath("$.title").value("Exportar gráficos de ventas"))
                .andExpect(jsonPath("$.isCompleted").value(false))
                .andExpect(jsonPath("$.position").value(1));
    }

    @Test
    @DisplayName("POST /subtasks - Should return 400 Bad Request when subtask title is too short")
    void shouldReturn400WhenTitleIsInvalid() throws Exception {
        when(createSubtaskUseCase.create(any(CreateSubtaskDtoService.class)))
                .thenThrow(SubtaskTitleException.tooShort());

        String jsonBody = """
                {
                  "taskId": 10,
                  "title": "A",
                  "position": 0
                }
                """;

        mockMvc.perform(post("/subtasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("subtask.title.too.short"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("GET /subtasks - Should return 200 OK with paginated subtasks for taskId")
    void shouldListSubtasksByTaskId() throws Exception {
        when(findSubtaskUseCase.findAllByTaskId(10L)).thenReturn(List.of(sampleSubtask));

        mockMvc.perform(get("/subtasks")
                        .param("taskId", "10")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(100))
                .andExpect(jsonPath("$.content[0].taskId").value(10))
                .andExpect(jsonPath("$.content[0].title").value("Exportar gráficos de ventas"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /subtasks/{id} - Should return 200 OK when subtask exists")
    void shouldFindSubtaskById() throws Exception {
        when(findSubtaskUseCase.findById(100L)).thenReturn(sampleSubtask);

        mockMvc.perform(get("/subtasks/{id}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.taskId").value(10))
                .andExpect(jsonPath("$.title").value("Exportar gráficos de ventas"));
    }

    @Test
    @DisplayName("GET /subtasks/{id} - Should return 404 Not Found when subtask does not exist")
    void shouldReturn404WhenSubtaskNotFound() throws Exception {
        when(findSubtaskUseCase.findById(999L)).thenThrow(SubtaskIdException.notFound());

        mockMvc.perform(get("/subtasks/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("subtask.id.not.found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("PUT /subtasks/{id} - Should return 200 OK with updated ResponseSubtaskDto")
    void shouldUpdateSubtaskAndReturn200() throws Exception {
        SubtaskModel updatedSubtask = sampleSubtask
                .changeTitle(SubtaskTitle.of("Gráficos validados"))
                .markCompleted()
                .changePosition(SubtaskPosition.of(2));

        when(updateSubtaskUseCase.update(any(UpdateSubtaskDtoService.class))).thenReturn(updatedSubtask);

        String jsonBody = """
                {
                  "title": "Gráficos validados",
                  "isCompleted": true,
                  "position": 2
                }
                """;

        mockMvc.perform(put("/subtasks/{id}", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.title").value("Gráficos validados"))
                .andExpect(jsonPath("$.isCompleted").value(true))
                .andExpect(jsonPath("$.position").value(2));
    }

    @Test
    @DisplayName("DELETE /subtasks/{id} - Should return 204 No Content")
    void shouldDeleteSubtaskAndReturn204() throws Exception {
        doNothing().when(deleteSubtaskUseCase).delete(100L);

        mockMvc.perform(delete("/subtasks/{id}", 100L))
                .andExpect(status().isNoContent());

        verify(deleteSubtaskUseCase).delete(100L);
    }
}

