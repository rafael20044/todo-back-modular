package com.rafael.todo.shared;

import lombok.Getter;

@Getter 
public class DomainException extends RuntimeException{

    private final String code;

    public DomainException(String code, String message){
        super(message);
        this.code = code;
    }

}
