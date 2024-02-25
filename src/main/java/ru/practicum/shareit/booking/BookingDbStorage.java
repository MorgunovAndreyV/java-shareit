package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

@Component("BookingDbStorage")
@RequiredArgsConstructor
public class BookingDbStorage implements BookingStorage {
    private final BookingRepository bookingRepository;
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    @Override
    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> getByBooker(Long id) {
        return bookingRepository.findByBookerId(id);
    }

    @Override
    public List<Booking> getByOwner(Long id) {
        return bookingRepository.findByItemOwnerId(id);
    }

    @Override
    public Booking addNew(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public Booking change(Booking booking) {
        Booking bookingFromBase = getById(booking.getId());
        BookingMapper.fillFromDto(BookingMapper.toDto(booking), bookingFromBase);

        return save(bookingFromBase);
    }

    @Override
    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }


    @Override
    public Booking getById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    @Override
    public List<Booking> getPastForBooker(Long userId) {
        return bookingRepository.findPastBookingsForBooker(userId);
    }

    @Override
    public List<Booking> getPastForOwner(Long userId) {
        return bookingRepository.findPastBookingsForOwner(userId);
    }

    @Override
    public List<Booking> getCurrentForBooker(Long userId) {
        return bookingRepository.findCurrentBookingsForBooker(userId);
    }

    @Override
    public List<Booking> getCurrentForOwner(Long userId) {
        return bookingRepository.findCurrentBookingsForOwner(userId);
    }

    @Override
    public List<Booking> getFutureForBooker(Long userId) {
        return bookingRepository.findFutureBookingsForBooker(userId);
    }

    @Override
    public List<Booking> getFutureForOwner(Long userId) {
        return bookingRepository.findFutureBookingsForOwner(userId);
    }

    @Override
    public List<Booking> getWaitingForBooker(Long userId) {
        return bookingRepository.findWaitingBookingsForBooker(userId);
    }

    @Override
    public List<Booking> getWaitingForOwner(Long userId) {
        return bookingRepository.findWaitingBookingsForOwner(userId);
    }

    @Override
    public List<Booking> getRejectedForBooker(Long userId) {
        return bookingRepository.findRejectedBookingsForBooker(userId);
    }

    @Override
    public List<Booking> getRejectedForOwner(Long userId) {
        return bookingRepository.findRejectedBookingsForOwner(userId);
    }

    @Override
    public Booking getLastBookingForItem(Long itemId) {
        return bookingRepository.findLastBookingIdForItem(itemId);
    }

    @Override
    public Booking getNextBookingForItem(Long itemId) {
        return bookingRepository.findNextBookingForItem(itemId);
    }

}
