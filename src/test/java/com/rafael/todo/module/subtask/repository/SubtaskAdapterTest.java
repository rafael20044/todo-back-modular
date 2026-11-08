package com.rafael.todo.module.subtask.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafael.todo.module.subtask.model.SubtaskModel;
import com.rafael.todo.module.subtask.model.vo.SubtaskCompleted;
import com.rafael.todo.module.subtask.model.vo.SubtaskId;
import com.rafael.todo.module.subtask.model.vo.SubtaskPosition;
import com.rafael.todo.module.subtask.model.vo.SubtaskTaskId;
import com.rafael.todo.module.subtask.model.vo.SubtaskTitle;
import com.rafael.todo.module.subtask.repository.adapter.imp.SubtaskAdapter;
import com.rafael.todo.module.subtask.repository.entity.SubtaskEntity;
import com.rafael.todo.module.subtask.repository.mapper.SubtaskMapperRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubtaskAdapter - Persistence Adapter Unit Tests")
class SubtaskAdapterTest {

    @Mock
    private ISubtaskRepository subtaskRepository;

    @InjectMocks
    private SubtaskAdapter subtaskAdapter;

    private SubtaskModel sampleModel;
    private SubtaskEntity sampleEntity;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        sampleModel = SubtaskModel.reconstitute(
                SubtaskId.of(100L),
                SubtaskTaskId.of(10L),
                SubtaskTitle.of("Subtarea 1"),
                SubtaskCompleted.pending(),
                SubtaskPosition.of(1),
                now,
                now
        );
        sampleEntity = SubtaskMapperRepository.toEntity(sampleModel);
    }

    @Test
    @DisplayName("Should persist new subtask entity and return reconstituted SubtaskModel in create")
    void shouldCreateSubtask() {
        SubtaskModel newModel = SubtaskModel.create(SubtaskTaskId.of(10L), SubtaskTitle.of("Subtarea 1"));
        when(subtaskRepository.save(any(SubtaskEntity.class))).thenReturn(sampleEntity);

        SubtaskModel created = subtaskAdapter.create(newModel);

        assertEquals(100L, created.getIdValue());
        assertEquals("Subtarea 1", created.getTitleValue());
        verify(subtaskRepository).save(any(SubtaskEntity.class));
    }

    @Test
    @DisplayName("Should update subtask entity and return reconstituted SubtaskModel")
    void shouldUpdateSubtask() {
        when(subtaskRepository.save(any(SubtaskEntity.class))).thenReturn(sampleEntity);

        SubtaskModel updated = subtaskAdapter.update(sampleModel);

        assertEquals(100L, updated.getIdValue());
        verify(subtaskRepository).save(any(SubtaskEntity.class));
    }

    @Test
    @DisplayName("Should find subtask by ID and map to SubtaskModel")
    void shouldFindById() {
        when(subtaskRepository.findById(100L)).thenReturn(Optional.of(sampleEntity));

        Optional<SubtaskModel> found = subtaskAdapter.findById(100L);

        assertTrue(found.isPresent());
        assertEquals(100L, found.get().getIdValue());
    }

    @Test
    @DisplayName("Should return empty Optional when subtask ID does not exist")
    void shouldReturnEmptyWhenIdNotFound() {
        when(subtaskRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<SubtaskModel> found = subtaskAdapter.findById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find all subtasks by taskId ordered by position")
    void shouldFindAllByTaskId() {
        when(subtaskRepository.findAllByTaskIdOrderByPositionAsc(10L))
                .thenReturn(List.of(sampleEntity));

        List<SubtaskModel> subtasks = subtaskAdapter.findAllByTaskId(10L);

        assertEquals(1, subtasks.size());
        assertEquals(100L, subtasks.getFirst().getIdValue());
        verify(subtaskRepository).findAllByTaskIdOrderByPositionAsc(10L);
    }

    @Test
    @DisplayName("Should delete subtask by ID in repository")
    void shouldDeleteById() {
        subtaskAdapter.delete(100L);

        verify(subtaskRepository).deleteById(100L);
    }
}

