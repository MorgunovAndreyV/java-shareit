package ru.practicum.shareit.handler;


import lombok.Getter;

@Getter
public class ExceptionResponse {
    private final String error;

    public ExceptionResponse(String error) {
        this.error = error;
    }

}
