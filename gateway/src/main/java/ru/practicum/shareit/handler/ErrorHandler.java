package ru.practicum.shareit.handler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.BookingControllerBadRequestException;
import ru.practicum.shareit.exception.ItemControllerBadRequestException;
import ru.practicum.shareit.exception.RequestControllerBadRequestException;
import ru.practicum.shareit.exception.UserControllerBadRequestException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleWrongUserData(final UserControllerBadRequestException e) {
        log.error(String.format(e.getMessage()));
        return new ExceptionResponse(
                String.format(e.getMessage())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleWrongItemData(final ItemControllerBadRequestException e) {
        log.error(String.format(e.getMessage()));
        return new ExceptionResponse(
                String.format(e.getMessage())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleWrongRequestData(final RequestControllerBadRequestException e) {
        log.error(String.format(e.getMessage()));
        return new ExceptionResponse(
                String.format(e.getMessage())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleBookingControllerBadRequest(final BookingControllerBadRequestException e) {
        log.error(String.format(e.getMessage()));
        return new ExceptionResponse(
                String.format(e.getMessage())
        );
    }

}
