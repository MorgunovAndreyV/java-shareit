package ru.practicum.shareit.exception;

public class BookingControllerBadRequestException extends RuntimeException {
    public BookingControllerBadRequestException(String message) {
        super(message);
    }
}
