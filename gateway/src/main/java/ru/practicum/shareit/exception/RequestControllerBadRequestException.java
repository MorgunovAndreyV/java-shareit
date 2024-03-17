package ru.practicum.shareit.exception;

public class RequestControllerBadRequestException extends RuntimeException {
    public RequestControllerBadRequestException(String message) {
        super(message);
    }
}
