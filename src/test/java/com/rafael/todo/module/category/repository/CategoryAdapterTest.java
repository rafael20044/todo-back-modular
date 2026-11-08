package com.rafael.todo.module.category.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafael.todo.module.category.model.CategoryModel;
import com.rafael.todo.module.category.model.vo.CategoryColor;
import com.rafael.todo.module.category.model.vo.CategoryId;
import com.rafael.todo.module.category.model.vo.CategoryName;
import com.rafael.todo.module.category.model.vo.CategoryUserId;
import com.rafael.todo.module.category.repository.adapter.imp.CategoryAdapter;
import com.rafael.todo.module.category.repository.entity.CategoryEntity;
import com.rafael.todo.module.category.repository.mapper.CategoryMapperRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryAdapter - Persistence Adapter Unit Tests")
class CategoryAdapterTest {

    @Mock
    private ICategoryRepository categoryRepository;

    @InjectMocks
    private CategoryAdapter categoryAdapter;

    private UUID sampleUserId;
    private CategoryModel sampleModel;
    private CategoryEntity sampleEntity;

    @BeforeEach
    void setUp() {
        sampleUserId = UUID.randomUUID();
        sampleModel = CategoryModel.reconstitute(
                CategoryId.of(1L),
                CategoryUserId.of(sampleUserId),
                CategoryName.of("Trabajo"),
                CategoryColor.of("#3B82F6"),
                LocalDateTime.now()
        );
        sampleEntity = CategoryMapperRepository.toEntity(sampleModel);
    }

    @Test
    @DisplayName("Should persist new category entity and return reconstituted model in create")
    void shouldCreateCategory() {
        CategoryModel newModel = CategoryModel.create(
                CategoryUserId.of(sampleUserId),
                CategoryName.of("Trabajo")
        );
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(sampleEntity);

        CategoryModel created = categoryAdapter.create(newModel);

        assertEquals(1L, created.getIdValue());
        assertEquals("Trabajo", created.getNameValue());
        verify(categoryRepository).save(any(CategoryEntity.class));
    }

    @Test
    @DisplayName("Should update category entity and return reconstituted model")
    void shouldUpdateCategory() {
        when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(sampleEntity);

        CategoryModel updated = categoryAdapter.update(sampleModel);

        assertEquals(1L, updated.getIdValue());
        assertEquals("Trabajo", updated.getNameValue());
        verify(categoryRepository).save(any(CategoryEntity.class));
    }

    @Test
    @DisplayName("Should find category by ID and map to CategoryModel")
    void shouldFindById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));

        Optional<CategoryModel> found = categoryAdapter.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getIdValue());
    }

    @Test
    @DisplayName("Should return empty Optional when category ID does not exist")
    void shouldReturnEmptyWhenIdNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<CategoryModel> found = categoryAdapter.findById(99L);

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find all categories filtered by userId")
    void shouldFindAllByUserId() {
        when(categoryRepository.findAllByUserId(sampleUserId)).thenReturn(List.of(sampleEntity));

        List<CategoryModel> categories = categoryAdapter.findAll(sampleUserId);

        assertEquals(1, categories.size());
        assertEquals(sampleUserId, categories.getFirst().getUserIdValue());
        verify(categoryRepository).findAllByUserId(sampleUserId);
    }

    @Test
    @DisplayName("Should find category by userId and name")
    void shouldFindByUserIdAndName() {
        when(categoryRepository.findByUserIdAndName(sampleUserId, "Trabajo"))
                .thenReturn(Optional.of(sampleEntity));

        Optional<CategoryModel> found = categoryAdapter.findByUserIdAndName(sampleUserId, "Trabajo");

        assertTrue(found.isPresent());
        assertEquals("Trabajo", found.get().getNameValue());
    }

    @Test
    @DisplayName("Should delete category by ID in repository")
    void shouldDeleteById() {
        categoryAdapter.delete(1L);

        verify(categoryRepository).deleteById(1L);
    }
}

