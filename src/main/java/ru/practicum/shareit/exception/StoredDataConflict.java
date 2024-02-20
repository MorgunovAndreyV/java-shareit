package ru.practicum.shareit.exception;

public class StoredDataConflict extends RuntimeException {

    public StoredDataConflict(String message) {
        super(message);
    }

}
