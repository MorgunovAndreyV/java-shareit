package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BookingValidationException;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

class BookingServiceTest {
    private static BookingService bookingService;
    private static BookingRepository bookingRepository;
    private static UserService userService;
    private static ItemService itemService;

    private static Item testItem;
    private static Item testItem2;
    private static Item testItem4;
    private static Item testItem5;

    private static User testUserOwner;
    private static User testUserOwner2;
    private static User testUserBooker;
    private static User testUserBooker2;
    private static BookingDto testBookingDto1;
    private static BookingDto testBookingDto2;
    private static BookingDto testBookingDto3;
    private static BookingDto testBookingDto4;
    private static BookingDto testBookingDto5;
    private static BookingDto testBookingDto6;
    private static BookingDto testBookingDto7;
    private static BookingDto testBookingDto8;
    private static Booking testBooking;
    private static Booking testBooking2;
    private static Booking testBooking3;
    private static Booking testBooking4;
    private static Booking testBooking5;


    @BeforeAll
    static void init() {
        bookingRepository = Mockito.mock(BookingRepository.class);
        userService = Mockito.mock(UserService.class);
        itemService = Mockito.mock(ItemService.class);
        bookingService = new BookingService(bookingRepository, userService, itemService);

        testUserOwner = User.builder()
                .id(1L)
                .name("user_owner1")
                .email("user_owner1@user.com")
                .build();

        testUserOwner2 = User.builder()
                .id(4L)
                .name("user_owner2")
                .email("user_owner2@user.com")
                .build();

        testUserBooker = User.builder()
                .id(2L)
                .name("bookerUser")
                .email("booker@user.com")
                .build();

        testUserBooker2 = User.builder()
                .id(3L)
                .name("bookerUser2")
                .email("booker2@user.com")
                .build();

        testItem = Item.builder()
                .id(1L)
                .name("Test item")
                .description("Just test item")
                .available(true)
                .owner(testUserOwner)
                .build();

        testItem2 = Item.builder()
                .id(2L)
                .name("Test item 2 unavailable")
                .description("Just test item 2")
                .available(false)
                .owner(testUserOwner)
                .build();

        testItem4 = Item.builder()
                .id(4L)
                .name("Test item 4")
                .description("Just test item 4")
                .available(true)
                .owner(testUserOwner)
                .build();

        testItem5 = Item.builder()
                .id(5L)
                .name("Test item 5")
                .description("Just test item 5")
                .available(true)
                .owner(testUserOwner)
                .build();

        testBookingDto1 = BookingDto.builder()
                .itemId(testItem.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        testBookingDto2 = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        testBookingDto3 = BookingDto.builder()
                .itemId(testItem2.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        testBookingDto4 = BookingDto.builder()
                .itemId(testItem2.getId())
                .end(LocalDateTime.now().plusDays(2))
                .build();

        testBookingDto5 = BookingDto.builder()
                .itemId(testItem2.getId())
                .start(LocalDateTime.now().plusDays(1))
                .build();

        testBookingDto6 = BookingDto.builder()
                .itemId(testItem2.getId())
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        testBookingDto7 = BookingDto.builder()
                .itemId(testItem2.getId())
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        testBookingDto8 = BookingDto.builder()
                .itemId(testItem2.getId())
                .start(LocalDateTime.parse("2034-03-08T17:13:37"))
                .end(LocalDateTime.parse("2034-03-08T17:13:37"))
                .build();

        testBooking = Booking.builder()
                .id(1L)
                .item(testItem)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .booker(testUserBooker2)
                .build();

        testBooking2 = Booking.builder()
                .id(2L)
                .item(testItem)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .booker(testUserBooker2)
                .build();

        testBooking3 = Booking.builder()
                .id(3L)
                .item(testItem)
                .status(BookingStatus.REJECTED)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .booker(testUserBooker2)
                .build();

        testBooking4 = Booking.builder()
                .id(4L)
                .item(testItem4)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .booker(testUserBooker2)
                .build();

        testBooking5 = Booking.builder()
                .id(5L)
                .item(testItem5)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .booker(testUserBooker2)
                .build();
    }

    @Test
    void testAddNew() {
        Mockito.when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(testUserBooker);
        Mockito.when(itemService.getItemById(Mockito.anyLong()))
                .thenReturn(testItem);
        Mockito.when(bookingRepository.findAll())
                .thenReturn(new ArrayList<>());

        BookingService bookingService = new BookingService(bookingRepository, userService, itemService);

        bookingService.addNew(testBookingDto1, testUserBooker.getId());

    }

    @Test
    void testAddNewFailedByEmptyItemId() {
        Exception e = Assertions.assertThrows(BookingValidationException.class, () -> {
            bookingService.addNew(testBookingDto2, testUserBooker.getId());
        });

        Assertions.assertEquals("ИД бронируемой вещи не может быть пустым", e.getMessage());

    }

    @Test
    void testAddNewFailedByNotAvailable() {
        Mockito.when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(testUserBooker);
        Mockito.when(itemService.getItemById(Mockito.anyLong()))
                .thenReturn(testItem2);

        Exception e = Assertions.assertThrows(BookingValidationException.class, () -> {
            bookingService.addNew(testBookingDto3, testUserBooker.getId());
        });

        Assertions.assertEquals("Бронируемая вещь не доступна", e.getMessage());

    }

    @Test
    void testAddNewEmptyStart() {
        Mockito.when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(testUserBooker);
        Mockito.when(itemService.getItemById(Mockito.anyLong()))
                .thenReturn(testItem);
        Exception e = Assertions.assertThrows(BookingValidationException.class, () -> {
            bookingService.addNew(testBookingDto4, testUserBooker.getId());
        });

        Assertions.assertEquals("Дата старта не может быть пустой", e.getMessage());

    }

    @Test
    void testAddNewEmptyEnd() {
        Mockito.when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(testUserBooker);
        Mockito.when(itemService.getItemById(Mockito.anyLong()))
                .thenReturn(testItem);

        Exception e = Assertions.assertThrows(BookingValidationException.class, () -> {
            bookingService.addNew(testBookingDto5, testUserBooker.getId());
        });

        Assertions.assertEquals("Дата окончания не может быть пустой", e.getMessage());

    }

    @Test
    void testAddNewEndBeforeStart() {
        Mockito.when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(testUserBooker);
        Mockito.when(itemService.getItemById(Mockito.anyLong()))
                .thenReturn(testItem);

        Exception e = Assertions.assertThrows(BookingValidationException.class, () -> {
            bookingService.addNew(testBookingDto6, testUserBooker.getId());
        });

        Assertions.assertEquals("Дата окончания не может быть раньше даты начала", e.getMessage());

    }

    @Test
    void testAddNewBookingPeriodInPast() {
        Mockito.when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(testUserBooker);
        Mockito.when(itemService.getItemById(Mockito.anyLong()))
                .thenReturn(testItem);

        Exception e = Assertions.assertThrows(BookingValidationException.class, () -> {
            bookingService.addNew(testBookingDto7, testUserBooker.getId());
        });

        Assertions.assertEquals("Границы периода бронирования не могут быть в прошлом", e.getMessage());

    }

    @Test
    void testAddNewStartEqualsEnd() {
        Mockito.when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(testUserBooker);
        Mockito.when(itemService.getItemById(Mockito.anyLong()))
                .thenReturn(testItem);

        Exception e = Assertions.assertThrows(BookingValidationException.class, () -> {
            bookingService.addNew(testBookingDto8, testUserBooker.getId());
        });

        Assertions.assertEquals("Окончание периода бронирования не может совпадать с его началом", e.getMessage());

    }

    @Test
    void testGetBookingByIdFilteredByUserFailedByEmptyUserId() {
        Long userId = null;

        Exception e = Assertions.assertThrows(BookingValidationException.class, () -> {
            bookingService.getBookingByIdFilteredByUser(testBooking.getId(), userId);
        });

        Assertions.assertEquals("Отсутствует id пользователя", e.getMessage());

    }

    @Test
    void testGetBookingByIdFilteredByUserFailedBookingOwnerShip() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(testBooking));

        Exception e = Assertions.assertThrows(RecordNotFoundException.class, () -> {
            bookingService.getBookingByIdFilteredByUser(testBooking.getId(), testUserBooker.getId());
        });

        Assertions.assertEquals("Бронирование с id " + testBooking.getId() +
                " не найдено для пользователя с id " + testUserBooker.getId(), e.getMessage());

    }

    @Test
    void testGetBookingByIdFilteredByUserFailedBookingNotFound() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(null));

        Exception e = Assertions.assertThrows(RecordNotFoundException.class, () -> {
            bookingService.getBookingByIdFilteredByUser(testBooking.getId(), testUserBooker.getId());
        });

        Assertions.assertEquals("Бронирование с id " + testBooking.getId() +
                " не найдено", e.getMessage());

    }

    @Test
    void testChangeBookingApprovalFailedByEmptyOwner() {
        Exception e = Assertions.assertThrows(BookingValidationException.class, () -> {
            bookingService.changeBookingApproval(testBooking2.getId(), null, true);
        });

        Assertions.assertEquals("Отсутствует id хозяина бронируемоей вещи", e.getMessage());

    }

    @Test
    void testChangeBookingApprovalSuccess() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(testBooking4));

        Assertions.assertEquals(BookingStatus.APPROVED,
                bookingService.changeBookingApproval(testBooking4.getId(), testUserOwner.getId(), true).getStatus());

    }

    @Test
    void testChangeBookingRejectionSuccess() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(testBooking5));

        Assertions.assertEquals(BookingStatus.REJECTED,
                bookingService.changeBookingApproval(testBooking5.getId(), testUserOwner.getId(),
                        false).getStatus());

    }

    @Test
    void testChangeBookingApprovalFailedBookingAlreadyApproved() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(testBooking2));

        Exception e = Assertions.assertThrows(BookingValidationException.class, () -> {
            bookingService.changeBookingApproval(testBooking2.getId(), testUserOwner.getId(), true);
        });

        Assertions.assertEquals("Бронирование уже подтверждено", e.getMessage());

    }

    @Test
    void testChangeBookingApprovalFailedBookingAlreadyRejected() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(testBooking3));

        Exception e = Assertions.assertThrows(BookingValidationException.class, () -> {
            bookingService.changeBookingApproval(testBooking3.getId(), testUserOwner.getId(), false);
        });

        Assertions.assertEquals("Бронирование уже отклонено", e.getMessage());

    }

    @Test
    void testChangeBookingApprovalFailedRecipientNotOwner() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(testBooking));

        Exception e = Assertions.assertThrows(RecordNotFoundException.class, () -> {
            bookingService.changeBookingApproval(testBooking.getId(), testUserOwner2.getId(), true);
        });

        Assertions.assertEquals("Для подтвеждения данным пользователем с id " + testUserOwner2.getId() +
                "не найдено бронирования с id " + testBooking.getId(), e.getMessage());

    }

    @Test
    void testGetBookingsFilteredByStateFailedByEmptyBooker() {
        Exception e = Assertions.assertThrows(BookingValidationException.class, () -> {
            bookingService.getBookingsFilteredByState(null, false, State.ALL, 1, 20);
        });

        Assertions.assertEquals("Отсутствует id автора брони вещи", e.getMessage());

    }

    @Test
    void testGetBookingsFilteredByStateFailedByEmptyOwner() {
        Exception e = Assertions.assertThrows(BookingValidationException.class, () -> {
            bookingService.getBookingsFilteredByState(null, true, State.ALL, 1, 20);
        });

        Assertions.assertEquals("Отсутствует id хозяина вещи", e.getMessage());

    }

    @Test
    void testAddNewBookingFailedByOwnerBookingHisItem() {
        Mockito.when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(testUserOwner);
        Mockito.when(itemService.getItemById(Mockito.anyLong()))
                .thenReturn(testItem);

        Exception e = Assertions.assertThrows(RecordNotFoundException.class, () -> {
            bookingService.addNew(testBookingDto1, testUserOwner.getId());
        });

        Assertions.assertEquals("Для пользователя с id " + testUserOwner.getId() + " не найдено доступной" +
                "для бронирования вещи с id " + testBookingDto1.getItemId(), e.getMessage());

    }

}