package ru.netology.demo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public class ExceptionResponse {
    private final String message;
    private final int id;

    @JsonCreator
    public ExceptionResponse(String message, int id) {
        this.message = message;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
