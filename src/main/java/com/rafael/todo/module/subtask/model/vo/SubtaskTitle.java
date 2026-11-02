package com.rafael.todo.module.subtask.model.vo;

import com.rafael.todo.module.subtask.exception.SubtaskTitleException;
import com.rafael.todo.shared.MyConst;

public record SubtaskTitle(String value) {

    public SubtaskTitle {
        if (value == null || value.trim().isBlank()) {
            throw SubtaskTitleException.required();
        }
        value = value.trim();
        int min = Integer.parseInt(MyConst.MIN_SUBTASK_TITLE_LENGTH.getValue());
        int max = Integer.parseInt(MyConst.MAX_SUBTASK_TITLE_LENGTH.getValue());
        if (value.length() < min) {
            throw SubtaskTitleException.tooShort();
        }
        if (value.length() > max) {
            throw SubtaskTitleException.tooLong();
        }
    }

    public static SubtaskTitle of(String value) {
        return new SubtaskTitle(value);
    }

    @Override
    public String toString() {
        return value;
    }

}

