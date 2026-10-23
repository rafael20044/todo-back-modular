package com.rafael.todo.module.category.model.vo;

import com.rafael.todo.module.category.exception.CategoryIdException;

public record CategoryId(Long value) {

    public CategoryId {
        if (value == null) {
            throw CategoryIdException.required();
        }
        if (value <= 0) {
            throw CategoryIdException.invalid();
        }
    }

    public static CategoryId of(Long value) {
        return new CategoryId(value);
    }

    public static CategoryId of(String value) {
        if (value == null || value.trim().isBlank()) {
            throw CategoryIdException.required();
        }
        try {
            return new CategoryId(Long.parseLong(value.trim()));
        } catch (NumberFormatException e) {
            throw CategoryIdException.invalid();
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
