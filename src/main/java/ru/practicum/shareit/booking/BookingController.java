package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.comparators.BookingComparators;
import ru.practicum.shareit.exception.BookingControllerBadRequestException;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addNew(@RequestHeader("X-Sharer-User-Id") Long id, @RequestBody BookingDto bookingDto) {
        return BookingMapper.toDto(bookingService.addNew(bookingDto, id));
    }

    @PatchMapping(path = "/{id}")
    public BookingDto change(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable("id") Long bookingId,
                             @RequestParam("approved") Boolean approval) {

        return BookingMapper.toDto(bookingService.changeBookingApproval(bookingId, id, approval));
    }

    @GetMapping(path = "/{id}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable("id") Long bookingId) {

        return BookingMapper.toDto(bookingService.getBookingByIdFilteredByUser(bookingId, id));
    }

    @GetMapping
    public Set<BookingDto> getAllForUserByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Nullable @RequestParam("state") String state,
                                                @Nullable @RequestParam(name = "from") Integer numberFrom,
                                                @Nullable @RequestParam(name = "size") Integer size) {
        Boolean isOwner = false;

        return bookingService.getBookingsFilteredByState(userId, isOwner, getState(state), numberFrom, size)
                .stream().map(BookingMapper::toDto).sorted(BookingComparators.compareBookingDtoByStartDateDesc)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @GetMapping(path = "/owner")
    public Set<BookingDto> getAllForOwnerByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Nullable @RequestParam("state") String state,
                                                 @Nullable @RequestParam(name = "from") Integer numberFrom,
                                                 @Nullable @RequestParam(name = "size") Integer size) {
        Boolean isOwner = true;

        return bookingService.getBookingsFilteredByState(userId, isOwner, getState(state), numberFrom, size)
                .stream().map(BookingMapper::toDto).sorted(BookingComparators.compareBookingDtoByStartDateDesc)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public State getState(String state) {
        if (state == null) {
            return State.ALL;
        }

        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BookingControllerBadRequestException("Unknown state: " + state);
        }

    }

}
