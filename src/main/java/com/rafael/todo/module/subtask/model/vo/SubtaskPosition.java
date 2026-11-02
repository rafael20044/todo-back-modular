package com.rafael.todo.module.subtask.model.vo;

import com.rafael.todo.module.subtask.exception.SubtaskPositionException;

public record SubtaskPosition(Integer value) {

    public SubtaskPosition {
        if (value == null) {
            value = 0;
        }
        if (value < 0) {
            throw SubtaskPositionException.invalid();
        }
    }

    public static SubtaskPosition of(Integer value) {
        return new SubtaskPosition(value);
    }

    public static SubtaskPosition of(int value) {
        return new SubtaskPosition(value);
    }

    public static SubtaskPosition defaultPosition() {
        return new SubtaskPosition(0);
    }

    @Override
    public String toString() {
        return value.toString();
    }

}

