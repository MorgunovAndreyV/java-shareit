package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    public List<Booking> getByBooker(Long id) {

        return bookingRepository.findByBookerId(id);
    }

    public Booking addNew(BookingDto bookingDto, Long id) {
        validateBookingData(bookingDto, id);

        User booker = userService.getUserById(id);

        bookingDto.setBooker(UserMapper.toDto(booker));
        bookingDto.setStatus(BookingStatus.WAITING);

        bookingAlreadyExists(BookingMapper.toEntity(bookingDto), bookingRepository.findAll());

        Booking newBooking = bookingRepository.save(BookingMapper.toEntity(bookingDto));

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
        Booking booking = bookingRepository.findById(bookingId).orElse(null);

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

            bookingRepository.save(booking);

        } else {
            throw new RecordNotFoundException("Для подтвеждения данным пользователем с id " + userId +
                    "не найдено бронирования с id " + id);
        }

        return booking;
    }

    public List<Booking> getBookingsFilteredByState(Long userId, Boolean isOwner, State state,
                                                    Integer numberFrom, Integer pageSize) {
        if (userId == null && !isOwner) {
            throw new BookingValidationException("Отсутствует id автора брони вещи");
        }

        if (userId == null) {
            throw new BookingValidationException("Отсутствует id хозяина вещи");
        }

        PageRequest pageRequest = null;

        if (pageSize != null && numberFrom != null) {
            if (pageSize < 1 || numberFrom < 0) {
                throw new BookingValidationException("Некорректные параметры запроса с постраничным выводом");

            }
            Sort sortByStartDate = Sort.by(Sort.Direction.DESC, "start");
            pageRequest = PageRequest.of(numberFrom > 0 ? numberFrom / pageSize : 0, pageSize, sortByStartDate);

        }

        userService.getUserById(userId);

        switch (state) {
            case ALL: {
                if (pageRequest != null) {
                    return !isOwner ? bookingRepository.findByBookerId(userId, pageRequest).toList() :
                            bookingRepository.findByItemOwnerId(userId, pageRequest).toList();
                } else {
                    return !isOwner ? bookingRepository.findByBookerId(userId) :
                            bookingRepository.findByItemOwnerId(userId);

                }

            }

            case PAST: {
                if (pageRequest != null) {
                    return !isOwner ? bookingRepository.findPastBookingsForBooker(userId, pageRequest).toList() :
                            bookingRepository.findPastBookingsForOwner(userId, pageRequest).toList();
                } else {
                    return !isOwner ? bookingRepository.findPastBookingsForBooker(userId) :
                            bookingRepository.findPastBookingsForOwner(userId);
                }

            }

            case FUTURE: {
                if (pageRequest != null) {
                    return !isOwner ? bookingRepository.findFutureBookingsForBooker(userId, pageRequest).toList() :
                            bookingRepository.findFutureBookingsForOwner(userId, pageRequest).toList();

                } else {
                    return !isOwner ? bookingRepository.findFutureBookingsForBooker(userId) :
                            bookingRepository.findFutureBookingsForOwner(userId);

                }

            }

            case CURRENT: {
                if (pageRequest != null) {
                    return !isOwner ? bookingRepository.findCurrentBookingsForBooker(userId, pageRequest).toList() :
                            bookingRepository.findCurrentBookingsForOwner(userId, pageRequest).toList();
                } else {
                    return !isOwner ? bookingRepository.findCurrentBookingsForBooker(userId) :
                            bookingRepository.findCurrentBookingsForOwner(userId);

                }

            }
            case WAITING: {
                if (pageRequest != null) {
                    return !isOwner ? bookingRepository.findWaitingBookingsForBooker(userId, pageRequest).toList() :
                            bookingRepository.findWaitingBookingsForOwner(userId, pageRequest).toList();
                } else {
                    return !isOwner ? bookingRepository.findWaitingBookingsForBooker(userId) :
                            bookingRepository.findWaitingBookingsForOwner(userId);

                }

            }
            case REJECTED: {
                if (pageRequest != null) {
                    return !isOwner ? bookingRepository.findRejectedBookingsForBooker(userId, pageRequest).toList() :
                            bookingRepository.findRejectedBookingsForOwner(userId, pageRequest).toList();
                } else {
                    return !isOwner ? bookingRepository.findRejectedBookingsForBooker(userId) :
                            bookingRepository.findRejectedBookingsForOwner(userId);

                }

            }
            default: {
                return null;
            }
        }
    }

    public Booking getLastBookingForItem(Long itemId) {
        return bookingRepository.findLastBookingIdForItem(itemId);
    }

    public Booking getNextBookingForItem(Long itemId) {
        return bookingRepository.findNextBookingForItem(itemId);
    }


    private void validateBookingData(BookingDto bookingDto, Long userId) {
        if (bookingDto.getItemId() == null) {
            throw new BookingValidationException("ИД бронируемой вещи не может быть пустым");

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
