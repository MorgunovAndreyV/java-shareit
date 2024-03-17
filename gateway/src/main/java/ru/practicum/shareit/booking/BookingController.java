package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BookingControllerBadRequestException;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addNew(@RequestHeader("X-Sharer-User-Id") Long id, @RequestBody BookingDto bookingDto) {
        return bookingClient.bookItem(id, bookingDto);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> change(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable("id") Long bookingId,
                                         @RequestParam("approved") Boolean approval) {
        return bookingClient.approveItemBooking(id, bookingId, approval);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable("id") Long bookingId) {

        return bookingClient.getBooking(id, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllForUserByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @Nullable @RequestParam(name = "state",
                                                               defaultValue = "ALL") String state,
                                                       @Nullable @RequestParam(name = "from",
                                                               defaultValue = "0") Integer numberFrom,
                                                       @Nullable @RequestParam(name = "size",
                                                               defaultValue = "10") Integer size) {
        validatePagination(numberFrom, size);

        return bookingClient.getAllForUserByState(userId, getState(state), numberFrom, size);
    }

    @GetMapping(path = "/owner")
    public ResponseEntity<Object> getAllForOwnerByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @Nullable @RequestParam(name = "state",
                                                                defaultValue = "ALL") String state,
                                                        @Nullable @RequestParam(name = "from",
                                                                defaultValue = "0") Integer numberFrom,
                                                        @Nullable @RequestParam(name = "size",
                                                                defaultValue = "10") Integer size) {
        validatePagination(numberFrom, size);

        return bookingClient.getAllForOwnerByState(userId, getState(state), numberFrom, size);
    }

    private BookingState getState(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BookingControllerBadRequestException("Unknown state: " + state);
        }

    }

    private void validatePagination(Integer numberFrom, Integer pageSize) {
        if (pageSize != null && numberFrom != null) {
            if (pageSize < 1 || numberFrom < 0) {
                throw new BookingControllerBadRequestException("Некорректные параметры запроса с постраничным выводом");

            }

        }

    }

}