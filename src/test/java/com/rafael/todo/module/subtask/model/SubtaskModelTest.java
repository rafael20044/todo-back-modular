package com.rafael.todo.module.subtask.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rafael.todo.module.subtask.model.vo.SubtaskCompleted;
import com.rafael.todo.module.subtask.model.vo.SubtaskId;
import com.rafael.todo.module.subtask.model.vo.SubtaskPosition;
import com.rafael.todo.module.subtask.model.vo.SubtaskTaskId;
import com.rafael.todo.module.subtask.model.vo.SubtaskTitle;

@DisplayName("SubtaskModel - Domain Model Unit Tests")
class SubtaskModelTest {

    @Test
    @DisplayName("Should create new SubtaskModel with default position 0 and isCompleted false")
    void shouldCreateSubtaskModelWithDefaultPosition() {
        SubtaskModel subtask = SubtaskModel.create(
                SubtaskTaskId.of(10L),
                SubtaskTitle.of("Revisar cifras")
        );

        assertNull(subtask.getIdValue());
        assertEquals(10L, subtask.getTaskIdValue());
        assertEquals("Revisar cifras", subtask.getTitleValue());
        assertFalse(subtask.isCompletedValue());
        assertEquals(0, subtask.getPositionValue());
        assertNotNull(subtask.getCreatedAt());
        assertNotNull(subtask.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create new SubtaskModel with explicit position")
    void shouldCreateSubtaskModelWithExplicitPosition() {
        SubtaskModel subtask = SubtaskModel.create(
                SubtaskTaskId.of(10L),
                SubtaskTitle.of("Exportar PDF"),
                SubtaskPosition.of(2)
        );

        assertEquals(10L, subtask.getTaskIdValue());
        assertEquals("Exportar PDF", subtask.getTitleValue());
        assertFalse(subtask.isCompletedValue());
        assertEquals(2, subtask.getPositionValue());
    }

    @Test
    @DisplayName("Should reconstitute SubtaskModel preserving all persisted attributes")
    void shouldReconstituteSubtaskModel() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 8, 1, 9, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 8, 2, 11, 30);

        SubtaskModel reconstituted = SubtaskModel.reconstitute(
                SubtaskId.of(50L),
                SubtaskTaskId.of(10L),
                SubtaskTitle.of("Subtarea persistida"),
                SubtaskCompleted.completed(),
                SubtaskPosition.of(3),
                createdAt,
                updatedAt
        );

        assertEquals(50L, reconstituted.getIdValue());
        assertEquals(10L, reconstituted.getTaskIdValue());
        assertEquals("Subtarea persistida", reconstituted.getTitleValue());
        assertTrue(reconstituted.isCompletedValue());
        assertEquals(3, reconstituted.getPositionValue());
        assertEquals(createdAt, reconstituted.getCreatedAt());
        assertEquals(updatedAt, reconstituted.getUpdatedAt());
    }

    @Test
    @DisplayName("Should perform copy-on-write mutations for title, position and completion state")
    void shouldPerformImmutableMutations() {
        SubtaskModel original = SubtaskModel.create(
                SubtaskTaskId.of(10L),
                SubtaskTitle.of("Original")
        );

        SubtaskModel renamed = original.changeTitle(SubtaskTitle.of("Renombrada"));
        assertNotSame(original, renamed);
        assertEquals("Original", original.getTitleValue());
        assertEquals("Renombrada", renamed.getTitleValue());

        SubtaskModel repositioned = renamed.changePosition(SubtaskPosition.of(5));
        assertEquals(5, repositioned.getPositionValue());

        SubtaskModel completed = repositioned.markCompleted();
        assertTrue(completed.isCompletedValue());

        SubtaskModel pending = completed.markPending();
        assertFalse(pending.isCompletedValue());

        SubtaskModel toggledTrue = pending.toggleCompleted();
        assertTrue(toggledTrue.isCompletedValue());

        SubtaskModel toggledFalse = toggledTrue.toggleCompleted();
        assertFalse(toggledFalse.isCompletedValue());
    }
}

