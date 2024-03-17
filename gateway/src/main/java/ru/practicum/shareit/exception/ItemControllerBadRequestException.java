package ru.practicum.shareit.exception;

public class ItemControllerBadRequestException extends RuntimeException {
    public ItemControllerBadRequestException(String message) {
        super(message);
    }
}
