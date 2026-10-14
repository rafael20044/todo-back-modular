package com.rafael.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.java.Log;

@SpringBootApplication
@Log
public class TodoApplication {

	public static void main(String[] args) {
		log.info("APLICACION INICIADA");
		SpringApplication.run(TodoApplication.class, args);
	}

}
