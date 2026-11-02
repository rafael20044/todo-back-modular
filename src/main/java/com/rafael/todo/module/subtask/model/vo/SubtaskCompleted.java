package com.rafael.todo.module.subtask.model.vo;

public record SubtaskCompleted(boolean value) {

    public static SubtaskCompleted of(boolean value) {
        return new SubtaskCompleted(value);
    }

    public static SubtaskCompleted completed() {
        return new SubtaskCompleted(true);
    }

    public static SubtaskCompleted pending() {
        return new SubtaskCompleted(false);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}

