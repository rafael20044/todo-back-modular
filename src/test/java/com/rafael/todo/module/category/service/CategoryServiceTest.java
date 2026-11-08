package com.rafael.todo.module.category.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafael.todo.module.category.exception.CategoryIdException;
import com.rafael.todo.module.category.model.CategoryModel;
import com.rafael.todo.module.category.model.vo.CategoryColor;
import com.rafael.todo.module.category.model.vo.CategoryId;
import com.rafael.todo.module.category.model.vo.CategoryName;
import com.rafael.todo.module.category.model.vo.CategoryUserId;
import com.rafael.todo.module.category.repository.adapter.ICreateCategoryAdapter;
import com.rafael.todo.module.category.repository.adapter.IDeleteCategoryAdapter;
import com.rafael.todo.module.category.repository.adapter.IFindCategoryAdapter;
import com.rafael.todo.module.category.repository.adapter.IUpdateCategoryAdapter;
import com.rafael.todo.module.category.service.dto.CreateCategoryDtoService;
import com.rafael.todo.module.category.service.dto.UpdateCategoryDtoService;
import com.rafael.todo.module.category.service.imp.CategoryService;
import com.rafael.todo.module.user.exception.UserUuidException;
import com.rafael.todo.module.user.model.UserModel;
import com.rafael.todo.module.user.model.vo.UserEmail;
import com.rafael.todo.module.user.model.vo.UserName;
import com.rafael.todo.module.user.model.vo.UserPassword;
import com.rafael.todo.module.user.service.IFindUserUseCase;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService - Use Cases Unit Tests")
class CategoryServiceTest {

    @Mock
    private ICreateCategoryAdapter createCategoryAdapter;

    @Mock
    private IUpdateCategoryAdapter updateCategoryAdapter;

    @Mock
    private IFindCategoryAdapter findCategoryAdapter;

    @Mock
    private IDeleteCategoryAdapter deleteCategoryAdapter;

    @Mock
    private IFindUserUseCase findUserUseCase;

    @InjectMocks
    private CategoryService categoryService;

    private UUID sampleUserId;
    private CategoryModel sampleCategory;
    private UserModel sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = UserModel.create(
                UserName.of("Rafael"),
                UserEmail.of("rafael@example.com"),
                UserPassword.fromRaw("Password123")
        );
        sampleUserId = sampleUser.getIdValue();

        sampleCategory = CategoryModel.reconstitute(
                CategoryId.of(1L),
                CategoryUserId.of(sampleUserId),
                CategoryName.of("Trabajo"),
                CategoryColor.of("#3B82F6"),
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("Create Category Use Case")
    class CreateCategoryTests {

        @Test
        @DisplayName("Should validate user existence via IFindUserUseCase and create category")
        void shouldCreateCategoryWhenUserExists() {
            CreateCategoryDtoService dto = new CreateCategoryDtoService(sampleUserId, "Trabajo", "#3B82F6");

            when(findUserUseCase.findById(sampleUserId)).thenReturn(sampleUser);
            when(createCategoryAdapter.create(any(CategoryModel.class))).thenReturn(sampleCategory);

            CategoryModel result = categoryService.create(dto);

            assertNotNull(result);
            assertEquals(1L, result.getIdValue());
            assertEquals("Trabajo", result.getNameValue());
            assertEquals("#3B82F6", result.getColorValue());
            verify(findUserUseCase).findById(sampleUserId);
            verify(createCategoryAdapter).create(any(CategoryModel.class));
        }

        @Test
        @DisplayName("Should propagate UserUuidException (404) when user does not exist")
        void shouldThrowWhenUserDoesNotExist() {
            UUID nonExistentUserId = UUID.randomUUID();
            CreateCategoryDtoService dto = new CreateCategoryDtoService(nonExistentUserId, "Trabajo", "#3B82F6");

            when(findUserUseCase.findById(nonExistentUserId)).thenThrow(UserUuidException.notFound());

            UserUuidException ex = assertThrows(UserUuidException.class, () -> categoryService.create(dto));

            assertEquals("user.uuid.not.found", ex.getCode());
            assertEquals(404, ex.getStatusCode());
            verify(createCategoryAdapter, never()).create(any());
        }
    }

    @Nested
    @DisplayName("Find Category Use Case")
    class FindCategoryTests {

        @Test
        @DisplayName("Should return CategoryModel when ID exists")
        void shouldReturnCategoryById() {
            when(findCategoryAdapter.findById(1L)).thenReturn(Optional.of(sampleCategory));

            CategoryModel result = categoryService.findById(1L);

            assertEquals(1L, result.getIdValue());
            assertEquals("Trabajo", result.getNameValue());
            verify(findCategoryAdapter).findById(1L);
        }

        @Test
        @DisplayName("Should throw CategoryIdException (404) when ID does not exist")
        void shouldThrowWhenCategoryNotFound() {
            when(findCategoryAdapter.findById(99L)).thenReturn(Optional.empty());

            CategoryIdException ex = assertThrows(CategoryIdException.class, () -> categoryService.findById(99L));

            assertEquals("category.id.not.found", ex.getCode());
            assertEquals(404, ex.getStatusCode());
        }

        @Test
        @DisplayName("Should return all categories filtered by userId")
        void shouldReturnAllCategoriesByUserId() {
            when(findCategoryAdapter.findAll(sampleUserId)).thenReturn(List.of(sampleCategory));

            List<CategoryModel> result = categoryService.findAll(sampleUserId);

            assertEquals(1, result.size());
            assertEquals(sampleUserId, result.getFirst().getUserIdValue());
            verify(findCategoryAdapter).findAll(sampleUserId);
        }
    }

    @Nested
    @DisplayName("Update Category Use Case")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Should update name and color when both are provided")
        void shouldUpdateNameAndColor() {
            UpdateCategoryDtoService updateDto = new UpdateCategoryDtoService(1L, "Trabajo Urgente", "#EF4444");

            when(findCategoryAdapter.findById(1L)).thenReturn(Optional.of(sampleCategory));
            when(updateCategoryAdapter.update(any(CategoryModel.class))).thenAnswer(inv -> inv.getArgument(0));

            CategoryModel updated = categoryService.update(updateDto);

            assertEquals("Trabajo Urgente", updated.getNameValue());
            assertEquals("#EF4444", updated.getColorValue());
            verify(updateCategoryAdapter).update(any(CategoryModel.class));
        }

        @Test
        @DisplayName("Should preserve existing name and color when update fields are null")
        void shouldPreserveFieldsWhenNullInUpdateDto() {
            UpdateCategoryDtoService partialDto = new UpdateCategoryDtoService(1L, null, null);

            when(findCategoryAdapter.findById(1L)).thenReturn(Optional.of(sampleCategory));
            when(updateCategoryAdapter.update(any(CategoryModel.class))).thenAnswer(inv -> inv.getArgument(0));

            CategoryModel updated = categoryService.update(partialDto);

            assertEquals(sampleCategory.getNameValue(), updated.getNameValue());
            assertEquals(sampleCategory.getColorValue(), updated.getColorValue());
        }

        @Test
        @DisplayName("Should throw CategoryIdException (404) when updating non-existent category")
        void shouldThrowWhenUpdatingNonExistentCategory() {
            UpdateCategoryDtoService updateDto = new UpdateCategoryDtoService(999L, "Nueva", "#10B981");

            when(findCategoryAdapter.findById(999L)).thenReturn(Optional.empty());

            CategoryIdException ex = assertThrows(CategoryIdException.class, () -> categoryService.update(updateDto));
            assertEquals(404, ex.getStatusCode());
            verify(updateCategoryAdapter, never()).update(any());
        }
    }

    @Nested
    @DisplayName("Delete Category Use Case")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Should delegate category deletion by ID to deleteCategoryAdapter")
        void shouldDeleteCategoryById() {
            categoryService.delete(1L);

            verify(deleteCategoryAdapter).delete(1L);
        }
    }
}

