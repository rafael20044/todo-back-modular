package com.rafael.todo.module.user.controller;

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

import com.rafael.todo.module.user.exception.UserEmailException;
import com.rafael.todo.module.user.exception.UserNameException;
import com.rafael.todo.module.user.exception.UserUuidException;
import com.rafael.todo.module.user.model.UserModel;
import com.rafael.todo.module.user.model.vo.UserEmail;
import com.rafael.todo.module.user.model.vo.UserName;
import com.rafael.todo.module.user.model.vo.UserPassword;
import com.rafael.todo.module.user.service.ICreateUserUseCase;
import com.rafael.todo.module.user.service.IDeleteUserUseCase;
import com.rafael.todo.module.user.service.IFindUserUseCase;
import com.rafael.todo.module.user.service.IUpdateUserUseCase;
import com.rafael.todo.module.user.service.dto.CreateUserDtoService;
import com.rafael.todo.module.user.service.dto.UpdateUserDtoService;
import com.rafael.todo.shared.GlobalExceptionController;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController - REST Endpoints & Exception Handling Tests")
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ICreateUserUseCase createUserUseCase;

    @Mock
    private IFindUserUseCase findUserUseCase;

    @Mock
    private IUpdateUserUseCase updateUserUseCase;

    @Mock
    private IDeleteUserUseCase deleteUserUseCase;

    @InjectMocks
    private UserController userController;

    private UserModel sampleUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionController())
                .build();

        sampleUser = UserModel.create(
                UserName.of("Rafael"),
                UserEmail.of("rafael@example.com"),
                UserPassword.fromRaw("Password123")
        );
    }

    @Test
    @DisplayName("POST /users - Should return 201 Created with Location header and ResponseUserDto")
    void shouldCreateUserAndReturn201() throws Exception {
        when(createUserUseCase.create(any(CreateUserDtoService.class))).thenReturn(sampleUser);

        String jsonBody = """
                {
                  "username": "Rafael",
                  "email": "rafael@example.com",
                  "password": "Password123",
                  "role": "ROLE_USER"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/" + sampleUser.getIdValue()))
                .andExpect(jsonPath("$.id").value(sampleUser.getIdValue().toString()))
                .andExpect(jsonPath("$.username").value("Rafael"))
                .andExpect(jsonPath("$.email").value("rafael@example.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    @DisplayName("POST /users - Should return 409 Conflict with ErrorDto when email already exists")
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        when(createUserUseCase.create(any(CreateUserDtoService.class)))
                .thenThrow(UserEmailException.alreadyExists());

        String jsonBody = """
                {
                  "username": "Rafael",
                  "email": "rafael@example.com",
                  "password": "Password123",
                  "role": "ROLE_USER"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("user.email.already.exists"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("POST /users - Should return 400 Bad Request with ErrorDto on domain validation error")
    void shouldReturn400WhenValidationFails() throws Exception {
        when(createUserUseCase.create(any(CreateUserDtoService.class)))
                .thenThrow(UserNameException.tooShort());

        String jsonBody = """
                {
                  "username": "R",
                  "email": "rafael@example.com",
                  "password": "Password123",
                  "role": "ROLE_USER"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("user.name.too.short"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("GET /users - Should return 200 OK with paginated ResponseUserDto list")
    void shouldListUsersPaginated() throws Exception {
        when(findUserUseCase.findAll()).thenReturn(List.of(sampleUser));

        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(sampleUser.getIdValue().toString()))
                .andExpect(jsonPath("$.content[0].username").value("Rafael"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /users/{uuid} - Should return 200 OK when user exists")
    void shouldFindUserById() throws Exception {
        UUID id = sampleUser.getIdValue();
        when(findUserUseCase.findById(id)).thenReturn(sampleUser);

        mockMvc.perform(get("/users/{uuid}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.username").value("Rafael"))
                .andExpect(jsonPath("$.email").value("rafael@example.com"));
    }

    @Test
    @DisplayName("GET /users/{uuid} - Should return 404 Not Found when user does not exist")
    void shouldReturn404WhenUserNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(findUserUseCase.findById(id)).thenThrow(UserUuidException.notFound());

        mockMvc.perform(get("/users/{uuid}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("user.uuid.not.found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("PUT /users/{uuid} - Should return 200 OK with updated ResponseUserDto")
    void shouldUpdateUserAndReturn200() throws Exception {
        UUID id = sampleUser.getIdValue();
        UserModel updatedModel = sampleUser.changeName(UserName.of("Rafael Updated"));
        when(updateUserUseCase.update(any(UpdateUserDtoService.class))).thenReturn(updatedModel);

        String jsonBody = """
                {
                  "username": "Rafael Updated",
                  "password": "NewPassword123",
                  "role": "ROLE_USER"
                }
                """;

        mockMvc.perform(put("/users/{uuid}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.username").value("Rafael Updated"));
    }

    @Test
    @DisplayName("DELETE /users/{uuid} - Should return 204 No Content")
    void shouldDeleteUserAndReturn204() throws Exception {
        UUID id = sampleUser.getIdValue();
        doNothing().when(deleteUserUseCase).delete(id);

        mockMvc.perform(delete("/users/{uuid}", id))
                .andExpect(status().isNoContent());

        verify(deleteUserUseCase).delete(id);
    }
}

