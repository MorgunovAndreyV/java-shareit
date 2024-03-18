package ru.practicum.shareit.exception;

public class UserControllerBadRequestException extends RuntimeException {
    public UserControllerBadRequestException(String message) {
        super(message);
    }
}
