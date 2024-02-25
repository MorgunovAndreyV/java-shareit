package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.BookingValidationException;
import ru.practicum.shareit.exception.ItemValidationException;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    @Qualifier("BookingDbStorage")
    private final BookingStorage bookingStorage;
    @Autowired
    private final UserService userService;
    @Autowired
    private final ItemService itemService;

    public List<Booking> getByBooker(Long id) {
        return bookingStorage.getByBooker(id);
    }

    public Booking addNew(BookingDto bookingDto, Long id) {
        bookingDataValidations(bookingDto, id);

        User booker = userService.getUserById(id);

        bookingDto.setBooker(UserMapper.toDto(booker));
        bookingDto.setStatus(BookingStatus.WAITING);

        bookingAlreadyExists(BookingMapper.toEntity(bookingDto), bookingStorage.getAll());

        Booking newBooking = bookingStorage.addNew(BookingMapper.toEntity(bookingDto));

        log.info("Вещь с id:" + bookingDto.getItem().getId() +
                " забронирована пользователем " + bookingDto.getBooker().getId());

        return newBooking;
    }

    public Booking getBookingByIdFilteredByUser(Long bookingId, Long userId) {
        if (userId == null) {
            throw new BookingValidationException("Отсутствует id пользователя");
        }

        Booking booking = getBookingById(bookingId);
        User booker = booking.getBooker();
        User owner = booking.getItem().getOwner();

        if (userId.equals(booker.getId()) || userId.equals(owner.getId())) {
            return booking;
        } else {
            throw new RecordNotFoundException("Бронирование с id " + bookingId +
                    " не найдено для пользователя с id " + userId);
        }

    }

    public Booking getBookingById(Long bookingId) throws RecordNotFoundException {
        Booking booking = bookingStorage.getById(bookingId);

        if (booking == null) {
            throw new RecordNotFoundException("Бронирование с id " + bookingId + " не найдено");
        }

        return booking;
    }

    public Booking changeBookingApproval(Long id, Long userId, Boolean approval) {
        if (userId == null) {
            throw new BookingValidationException("Отсутствует id хозяина бронируемоей вещи");
        }

        Booking booking = getBookingById(id);

        if (userId.equals(booking.getItem().getOwner().getId())) {
            if (approval != null) {
                if (approval) {
                    if (!BookingStatus.APPROVED.equals(booking.getStatus())) {
                        booking.approveBooking();
                        log.info("Бронирование с id:" + id + "было подтверждено");

                    } else {
                        throw new BookingValidationException("Бронирование уже подтверждено");
                    }


                } else {
                    if (!BookingStatus.REJECTED.equals(booking.getStatus())) {
                        booking.rejectBooking();
                        log.info("Бронирование с id:" + id + "было отклонено");

                    } else {
                        throw new BookingValidationException("Бронирование уже отклонено");
                    }


                }

            }

            bookingStorage.save(booking);

        } else {
            throw new RecordNotFoundException("Для подтвеждения данным пользователем с id " + id +
                    "не найдено бронирования с id " + id);
        }

        return booking;
    }

    public List<Booking> getBookingsFilteredByState(Long userId, Boolean isOwner, State state) {
        if (userId == null && !isOwner) {
            throw new BookingValidationException("Отсутствует id автора брони вещи");
        }

        if (userId == null) {
            throw new BookingValidationException("Отсутствует id хозяина вещи");
        }

        userService.getUserById(userId);

        switch (state) {
            case ALL: {
                return !isOwner ? bookingStorage.getByBooker(userId) : bookingStorage.getByOwner(userId);
            }

            case PAST: {
                return !isOwner ? bookingStorage.getPastForBooker(userId) : bookingStorage.getPastForOwner(userId);
            }

            case FUTURE: {
                return !isOwner ? bookingStorage.getFutureForBooker(userId) : bookingStorage.getFutureForOwner(userId);
            }

            case CURRENT: {
                return !isOwner ? bookingStorage.getCurrentForBooker(userId) : bookingStorage.getCurrentForOwner(userId);
            }
            case WAITING: {
                return !isOwner ? bookingStorage.getWaitingForBooker(userId) : bookingStorage.getWaitingForOwner(userId);
            }
            case REJECTED: {
                return !isOwner ? bookingStorage.getRejectedForBooker(userId) :
                        bookingStorage.getRejectedForOwner(userId);
            }
            default: {
                return null;
            }
        }
    }

    public Booking getLastBookingForItem(Long itemId) {
        return bookingStorage.getLastBookingForItem(itemId);
    }

    public Booking getNextBookingForItem(Long itemId) {
        return bookingStorage.getNextBookingForItem(itemId);
    }


    private void bookingDataValidations(BookingDto bookingDto, Long userId) {
        if (bookingDto.getItemId() == null) {
            throw new BookingValidationException("ИД бронируемой вещи не может быть пустой");

        }

        Item item = itemService.getItemById(bookingDto.getItemId());

        if (userId.equals(item.getOwner().getId())) {
            throw new RecordNotFoundException("Для пользователя с id " + userId + " не найдено доступной" +
                    "для бронирования вещи с id " + bookingDto.getItemId());
        }

        if (!item.getAvailable()) {
            throw new BookingValidationException("Бронируемая вещь не доступна");

        } else {
            bookingDto.setItem(ItemMapper.toDto(item));
        }

        if (bookingDto.getStart() == null) {
            throw new BookingValidationException("Дата старта не может быть пустой");

        }

        if (bookingDto.getEnd() == null) {
            throw new BookingValidationException("Дата окончания не может быть пустой");

        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BookingValidationException("Дата окончания не может быть раньше даты начала");
        }

        if (bookingDto.getEnd().isBefore(LocalDateTime.now()) || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingValidationException("Границы периода бронирования не могут быть в прошлом");
        }


        if (bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new BookingValidationException("Окончание периода бронирования не может совпадать с его началом");
        }

    }

    private void bookingAlreadyExists(Booking booking, List<Booking> bookingList) {
        if (bookingList.contains(booking)) {
            throw new ItemValidationException("Такая бронь уже добавлена");
        }

    }

    public ItemDto setLastBooking(ItemDto itemDto) {
        Booking lastBooking = getLastBookingForItem(itemDto.getId());

        itemDto.setLastBooking(lastBooking != null ? BookingMapper.toDto(lastBooking) : null);

        return itemDto;
    }

    public ItemDto setNextBooking(ItemDto itemDto) {
        Booking nextBooking = getNextBookingForItem(itemDto.getId());

        itemDto.setNextBooking(nextBooking != null ? BookingMapper.toDto(nextBooking) : null);

        return itemDto;
    }

}
