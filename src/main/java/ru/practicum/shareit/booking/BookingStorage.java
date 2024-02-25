package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingStorage {
    List<Booking> getAll();

    List<Booking> getByBooker(Long id);

    List<Booking> getByOwner(Long id);

    Booking addNew(Booking booking);

    Booking change(Booking booking);

    Booking save(Booking booking);

    Booking getById(Long id);

    List<Booking> getPastForBooker(Long userId);

    List<Booking> getPastForOwner(Long userId);

    List<Booking> getCurrentForBooker(Long userId);

    List<Booking> getCurrentForOwner(Long userId);

    List<Booking> getFutureForBooker(Long userId);

    List<Booking> getFutureForOwner(Long userId);

    List<Booking> getWaitingForBooker(Long userId);

    List<Booking> getWaitingForOwner(Long userId);

    List<Booking> getRejectedForBooker(Long userId);

    List<Booking> getRejectedForOwner(Long userId);

    Booking getLastBookingForItem(Long itemId);

    Booking getNextBookingForItem(Long itemId);

}