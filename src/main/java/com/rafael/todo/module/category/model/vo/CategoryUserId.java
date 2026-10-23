package com.rafael.todo.module.category.model.vo;

import java.util.UUID;

import com.rafael.todo.module.category.exception.CategoryUserIdException;

public record CategoryUserId(UUID value) {

    public CategoryUserId {
        if (value == null) {
            throw CategoryUserIdException.required();
        }
    }

    public static CategoryUserId of(UUID value) {
        return new CategoryUserId(value);
    }

    public static CategoryUserId of(String value) {
        if (value == null || value.trim().isBlank()) {
            throw CategoryUserIdException.required();
        }
        try {
            return new CategoryUserId(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException e) {
            throw CategoryUserIdException.invalidFormat();
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
