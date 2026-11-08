package com.rafael.todo.module.category.controller;

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

import com.rafael.todo.module.category.exception.CategoryColorException;
import com.rafael.todo.module.category.exception.CategoryIdException;
import com.rafael.todo.module.category.exception.CategoryNameException;
import com.rafael.todo.module.category.model.CategoryModel;
import com.rafael.todo.module.category.model.vo.CategoryColor;
import com.rafael.todo.module.category.model.vo.CategoryId;
import com.rafael.todo.module.category.model.vo.CategoryName;
import com.rafael.todo.module.category.model.vo.CategoryUserId;
import com.rafael.todo.module.category.service.ICreateCategoryUseCase;
import com.rafael.todo.module.category.service.IDeleteCategoryUseCase;
import com.rafael.todo.module.category.service.IFindCategoryUseCase;
import com.rafael.todo.module.category.service.IUpdateCategoryUseCase;
import com.rafael.todo.module.category.service.dto.CreateCategoryDtoService;
import com.rafael.todo.module.category.service.dto.UpdateCategoryDtoService;
import com.rafael.todo.shared.GlobalExceptionController;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryController - REST Endpoints & Exception Handling Tests")
class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IFindCategoryUseCase findCategoryUseCase;

    @Mock
    private ICreateCategoryUseCase createCategoryUseCase;

    @Mock
    private IUpdateCategoryUseCase updateCategoryUseCase;

    @Mock
    private IDeleteCategoryUseCase deleteCategoryUseCase;

    @InjectMocks
    private CategoryController categoryController;

    private UUID sampleUserId;
    private CategoryModel sampleCategory;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setControllerAdvice(new GlobalExceptionController())
                .build();

        sampleUserId = UUID.randomUUID();
        sampleCategory = CategoryModel.reconstitute(
                CategoryId.of(1L),
                CategoryUserId.of(sampleUserId),
                CategoryName.of("Trabajo"),
                CategoryColor.of("#3B82F6"),
                LocalDateTime.of(2026, 5, 10, 12, 0)
        );
    }

    @Test
    @DisplayName("POST /categories - Should return 201 Created with Location header and ResponseCategoryDto")
    void shouldCreateCategoryAndReturn201() throws Exception {
        when(createCategoryUseCase.create(any(CreateCategoryDtoService.class))).thenReturn(sampleCategory);

        String jsonBody = String.format("""
                {
                  "userId": "%s",
                  "name": "Trabajo",
                  "color": "#3B82F6"
                }
                """, sampleUserId);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/categories/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(sampleUserId.toString()))
                .andExpect(jsonPath("$.name").value("Trabajo"))
                .andExpect(jsonPath("$.color").value("#3B82F6"));
    }

    @Test
    @DisplayName("POST /categories - Should return 400 Bad Request on invalid color format")
    void shouldReturn400WhenColorIsInvalid() throws Exception {
        when(createCategoryUseCase.create(any(CreateCategoryDtoService.class)))
                .thenThrow(CategoryColorException.invalidFormat());

        String jsonBody = String.format("""
                {
                  "userId": "%s",
                  "name": "Trabajo",
                  "color": "invalid-color"
                }
                """, sampleUserId);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("category.color.invalid.format"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /categories - Should return 409 Conflict when category name already exists for user")
    void shouldReturn409WhenCategoryNameAlreadyExists() throws Exception {
        when(createCategoryUseCase.create(any(CreateCategoryDtoService.class)))
                .thenThrow(CategoryNameException.alreadyExists());

        String jsonBody = String.format("""
                {
                  "userId": "%s",
                  "name": "Trabajo",
                  "color": "#3B82F6"
                }
                """, sampleUserId);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("category.name.already.exists"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("GET /categories - Should return 200 OK with paginated categories filtered by userId")
    void shouldListCategoriesPaginated() throws Exception {
        when(findCategoryUseCase.findAll(sampleUserId)).thenReturn(List.of(sampleCategory));

        mockMvc.perform(get("/categories")
                        .param("userId", sampleUserId.toString())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Trabajo"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /categories/{id} - Should return 200 OK when category exists")
    void shouldFindCategoryById() throws Exception {
        when(findCategoryUseCase.findById(1L)).thenReturn(sampleCategory);

        mockMvc.perform(get("/categories/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Trabajo"))
                .andExpect(jsonPath("$.color").value("#3B82F6"));
    }

    @Test
    @DisplayName("GET /categories/{id} - Should return 404 Not Found when category does not exist")
    void shouldReturn404WhenCategoryNotFound() throws Exception {
        when(findCategoryUseCase.findById(99L)).thenThrow(CategoryIdException.notFound());

        mockMvc.perform(get("/categories/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("category.id.not.found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("PUT /categories/{id} - Should return 200 OK with updated ResponseCategoryDto")
    void shouldUpdateCategoryAndReturn200() throws Exception {
        CategoryModel updatedModel = sampleCategory
                .changeName(CategoryName.of("Trabajo Urgente"))
                .changeColor(CategoryColor.of("#EF4444"));

        when(updateCategoryUseCase.update(any(UpdateCategoryDtoService.class))).thenReturn(updatedModel);

        String jsonBody = """
                {
                  "name": "Trabajo Urgente",
                  "color": "#EF4444"
                }
                """;

        mockMvc.perform(put("/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Trabajo Urgente"))
                .andExpect(jsonPath("$.color").value("#EF4444"));
    }

    @Test
    @DisplayName("DELETE /categories/{id} - Should return 204 No Content")
    void shouldDeleteCategoryAndReturn204() throws Exception {
        doNothing().when(deleteCategoryUseCase).delete(1L);

        mockMvc.perform(delete("/categories/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(deleteCategoryUseCase).delete(1L);
    }
}

