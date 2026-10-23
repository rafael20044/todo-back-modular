package com.rafael.todo.shared;

import lombok.Getter;

@Getter 
public class DomainException extends RuntimeException{

    private final String code;
    private final int statusCode;

    public DomainException(String code, String message, int statusCode){
        super(message);
        this.code = code;
        this.statusCode = statusCode;
    }

}
