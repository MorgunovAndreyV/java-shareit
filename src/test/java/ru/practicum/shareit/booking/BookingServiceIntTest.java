package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.config.ContextConfig;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({ContextConfig.class, BookingService.class})
class BookingServiceIntTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private static Item testItem;
    private static Item testItem2;
    private static Item testItem3;
    private static Item testItem4;

    private static User testUserOwner;
    private static User testUserOwner2;
    private static User testUserBooker;
    private static User testUserBooker2;
    private static BookingDto testBookingDto1;
    private static BookingDto testBookingDto2;
    private static BookingDto testBookingDto3;
    private static BookingDto testBookingDto4;

    private static Booking testBooking;
    private static Booking testBooking2;
    private static Booking testBooking3;
    private static Booking testBooking4;

    @BeforeAll
    static void init() {
        testUserOwner = User.builder()
                .name("user_owner1")
                .email("user_owner1@user.com")
                .build();

        testUserOwner2 = User.builder()
                .name("user_owner2")
                .email("user_owner2@user.com")
                .build();

        testUserBooker = User.builder()
                .name("bookerUser")
                .email("booker@user.com")
                .build();

        testUserBooker2 = User.builder()
                .name("bookerUser2")
                .email("booker2@user.com")
                .build();

        testItem = Item.builder()
                .name("Test item")
                .description("Just test item")
                .available(true)
                .build();

        testItem2 = Item.builder()
                .name("Test item 2")
                .description("Just test item 2")
                .available(true)
                .build();

        testItem3 = Item.builder()
                .name("Test item 3")
                .description("Just test item 3")
                .available(true)
                .build();

        testItem4 = Item.builder()
                .name("Test item 4")
                .description("Just test item 4")
                .available(true)
                .build();

        testBookingDto1 = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        testBookingDto2 = BookingDto.builder()
                .build();

        testBookingDto3 = BookingDto.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        testBookingDto4 = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        testBooking = Booking.builder()
                .status(BookingStatus.WAITING)
                .build();

        testBooking2 = Booking.builder()
                .status(BookingStatus.WAITING)
                .build();

        testBooking3 = Booking.builder()
                .status(BookingStatus.WAITING)
                .build();

        testBooking4 = Booking.builder()
                .status(BookingStatus.WAITING)
                .build();

    }

    @BeforeEach
    void prepareData() {
        if (testUserOwner.getId() == null) {
            testUserOwner = userService.addNew(testUserOwner);
        }

        if (testUserBooker.getId() == null) {
            testUserBooker = userService.addNew(testUserBooker);
        }

        if (testUserOwner2.getId() == null) {
            testUserOwner2 = userService.addNew(testUserOwner2);
        }

        if (testUserBooker2.getId() == null) {
            testUserBooker2 = userService.addNew(testUserBooker2);
        }

        if (testItem.getId() == null) {
            testItem = itemService.addNew(testItem, testUserOwner.getId());
        }

        if (testItem2.getId() == null) {
            testItem2 = itemService.addNew(testItem2, testUserOwner2.getId());
        }

        if (testItem3.getId() == null) {
            testItem3 = itemService.addNew(testItem3, testUserOwner.getId());
        }

        if (testItem4.getId() == null) {
            testItem4 = itemService.addNew(testItem4, testUserOwner.getId());
        }

        if (testBooking.getId() == null) {
            testBookingDto1.setItemId(testItem.getId());
            Long bookingId1 = bookingService.addNew(testBookingDto1, testUserBooker.getId()).getId();
            testBooking = bookingService.getBookingById(bookingId1);
        }

        if (testBooking4.getId() == null) {
            testBookingDto4.setItemId(testItem3.getId());

            Long bookingId4 = bookingService.addNew(testBookingDto4, testUserBooker.getId()).getId();
            testBooking4 = bookingService.changeBookingApproval(bookingId4, testUserOwner.getId(), false);
        }

        if (testBooking2.getId() == null) {
            testBookingDto2.setItemId(testItem2.getId());
            testBookingDto2.setStart(LocalDateTime.now().plusNanos(30000000));
            testBookingDto2.setEnd(LocalDateTime.now().plusNanos(40000000));

            Long bookingId2 = bookingService.addNew(testBookingDto2, testUserBooker.getId()).getId();
            testBooking2 = bookingService.getBookingById(bookingId2);
        }

        if (testBooking3.getId() == null) {
            testBookingDto3.setItemId(testItem2.getId());
            testBookingDto3.setStart(LocalDateTime.now().plusNanos(30000000));
            Long bookingId3 = bookingService.addNew(testBookingDto3, testUserBooker.getId()).getId();

            testBooking3 = bookingService.getBookingById(bookingId3);
        }

    }

    @Test
    void testGetBookingsFilteredByStateFUTURE() {
        List<Booking> foundBookingsBooker = bookingService
                .getBookingsFilteredByState(testUserBooker.getId(), false, State.FUTURE, 0, 5);
        Assertions.assertTrue(foundBookingsBooker.contains(testBooking));

        List<Booking> foundBookingsOwner = bookingService
                .getBookingsFilteredByState(testUserOwner.getId(), true, State.FUTURE, 0, 5);
        Assertions.assertTrue(foundBookingsOwner.contains(testBooking));

        List<Booking> foundBookingsBookerNoPages = bookingService
                .getBookingsFilteredByState(testUserBooker.getId(), false, State.FUTURE, null, null);
        Assertions.assertTrue(foundBookingsBookerNoPages.contains(testBooking));

        List<Booking> foundBookingsOwnerNoPages = bookingService
                .getBookingsFilteredByState(testUserOwner.getId(), true, State.FUTURE, null, null);
        Assertions.assertTrue(foundBookingsOwnerNoPages.contains(testBooking));
    }

    @Test
    void testGetBookingsFilteredByStatePAST() {
        List<Booking> foundBookingsBooker = bookingService
                .getBookingsFilteredByState(testUserBooker.getId(), false, State.PAST, 0, 5);
        Assertions.assertTrue(foundBookingsBooker.contains(testBooking2));

        List<Booking> foundBookingsOwner = bookingService
                .getBookingsFilteredByState(testUserOwner2.getId(), true, State.PAST, 0, 5);
        Assertions.assertTrue(foundBookingsOwner.contains(testBooking2));

        List<Booking> foundBookingsBookerNoPages = bookingService
                .getBookingsFilteredByState(testUserBooker.getId(), false, State.PAST, null, null);
        Assertions.assertTrue(foundBookingsBookerNoPages.contains(testBooking2));

        List<Booking> foundBookingsOwnerNoPages = bookingService
                .getBookingsFilteredByState(testUserOwner2.getId(), true, State.PAST, null, null);
        Assertions.assertTrue(foundBookingsOwnerNoPages.contains(testBooking2));
    }

    @Test
    void testGetBookingsFilteredByStateWAITING() {
        List<Booking> foundBookingsBooker = bookingService
                .getBookingsFilteredByState(testUserBooker.getId(), false, State.WAITING, 0, 5);
        Assertions.assertTrue(foundBookingsBooker.contains(testBooking));

        List<Booking> foundBookingsOwner = bookingService
                .getBookingsFilteredByState(testUserOwner.getId(), true, State.WAITING, 0, 5);
        Assertions.assertTrue(foundBookingsOwner.contains(testBooking));

        List<Booking> foundBookingsOwner2 = bookingService
                .getBookingsFilteredByState(testUserOwner2.getId(), true, State.WAITING, 0, 5);
        Assertions.assertTrue(foundBookingsOwner2.contains(testBooking2));

        List<Booking> foundBookingsBookerNoPages = bookingService
                .getBookingsFilteredByState(testUserBooker.getId(), false, State.WAITING, null, null);
        Assertions.assertTrue(foundBookingsBookerNoPages.contains(testBooking));

        List<Booking> foundBookingsOwnerNoPages = bookingService
                .getBookingsFilteredByState(testUserOwner.getId(), true, State.WAITING, null, null);
        Assertions.assertTrue(foundBookingsOwnerNoPages.contains(testBooking));

        List<Booking> foundBookingsOwner2NoPages = bookingService
                .getBookingsFilteredByState(testUserOwner2.getId(), true, State.WAITING, null, null);
        Assertions.assertTrue(foundBookingsOwner2NoPages.contains(testBooking2));
    }

    @Test
    void testGetBookingsFilteredByStateCURRENT() {
        List<Booking> foundBookings = bookingService
                .getBookingsFilteredByState(testUserBooker.getId(), false, State.CURRENT, 0, 5);
        Assertions.assertTrue(foundBookings.contains(testBooking3));

        List<Booking> foundBookingsOwner = bookingService
                .getBookingsFilteredByState(testUserOwner2.getId(), true, State.CURRENT, 0, 5);
        Assertions.assertTrue(foundBookingsOwner.contains(testBooking3));

        List<Booking> foundBookingsNoPages = bookingService
                .getBookingsFilteredByState(testUserBooker.getId(), false, State.CURRENT, null, null);
        Assertions.assertTrue(foundBookingsNoPages.contains(testBooking3));

        List<Booking> foundBookingsOwnerNoPages = bookingService
                .getBookingsFilteredByState(testUserOwner2.getId(), true, State.CURRENT, null, null);
        Assertions.assertTrue(foundBookingsOwnerNoPages.contains(testBooking3));
    }

    @Test
    void testGetBookingsFilteredByStateREJECTED() {
        List<Booking> foundBookings = bookingService
                .getBookingsFilteredByState(testUserBooker.getId(), false, State.REJECTED, 0, 5);
        Assertions.assertTrue(foundBookings.contains(testBooking4));

        List<Booking> foundBookings2 = bookingService
                .getBookingsFilteredByState(testUserBooker.getId(), false, State.REJECTED, null, null);
        Assertions.assertTrue(foundBookings2.contains(testBooking4));

        List<Booking> foundBookingsOwner = bookingService
                .getBookingsFilteredByState(testUserOwner.getId(), true, State.REJECTED, 0, 5);
        Assertions.assertTrue(foundBookings.contains(testBooking4));

        List<Booking> foundBookings2Owner = bookingService
                .getBookingsFilteredByState(testUserOwner.getId(), true, State.REJECTED, null, null);
        Assertions.assertTrue(foundBookings2.contains(testBooking4));
    }

    @Test
    void testGetBookingsFilteredByStateALL() {
        List<Booking> foundBookings = bookingService
                .getBookingsFilteredByState(testUserBooker.getId(), false, State.ALL, 0, 5);

        List<Booking> foundBookingsNoPage = bookingService
                .getBookingsFilteredByState(testUserBooker.getId(), false, State.ALL, null, null);


        Assertions.assertTrue(foundBookings.contains(testBooking4));
        Assertions.assertTrue(foundBookings.contains(testBooking3));
        Assertions.assertTrue(foundBookings.contains(testBooking2));
        Assertions.assertTrue(foundBookings.contains(testBooking));

        Assertions.assertTrue(foundBookingsNoPage.contains(testBooking4));
        Assertions.assertTrue(foundBookingsNoPage.contains(testBooking3));
        Assertions.assertTrue(foundBookingsNoPage.contains(testBooking2));
        Assertions.assertTrue(foundBookingsNoPage.contains(testBooking));

        List<Booking> foundBookingsOwner = bookingService
                .getBookingsFilteredByState(testUserOwner.getId(), true, State.ALL, 0, 5);

        List<Booking> foundBookingsOwnerNoPage = bookingService
                .getBookingsFilteredByState(testUserOwner.getId(), true, State.ALL, null, null);


        Assertions.assertTrue(foundBookingsOwner.contains(testBooking4));
        Assertions.assertFalse(foundBookingsOwner.contains(testBooking3));
        Assertions.assertFalse(foundBookingsOwner.contains(testBooking2));
        Assertions.assertTrue(foundBookingsOwner.contains(testBooking));

        Assertions.assertTrue(foundBookingsOwnerNoPage.contains(testBooking4));
        Assertions.assertFalse(foundBookingsOwnerNoPage.contains(testBooking3));
        Assertions.assertFalse(foundBookingsOwnerNoPage.contains(testBooking2));
        Assertions.assertTrue(foundBookingsOwnerNoPage.contains(testBooking));
    }

    @Test
    void testGetLastBookingForItemDto() {

        ItemDto itemDto = ItemMapper.toDto(testItem2);
        bookingService.setLastBooking(itemDto);

        BookingDto bookingDto = BookingMapper.toDto(testBooking3);
        bookingDto.setBookerId(bookingDto.getBooker().getId());

        Assertions.assertTrue(bookingDto.equals(itemDto.getLastBooking()));

    }

    @Test
    void testGetNextBookingForItemDto() {

        ItemDto itemDto = ItemMapper.toDto(testItem);
        bookingService.setNextBooking(itemDto);

        BookingDto bookingDto = BookingMapper.toDto(testBooking);
        bookingDto.setBookerId(bookingDto.getBooker().getId());

        Assertions.assertTrue(bookingDto.equals(itemDto.getNextBooking()));

    }

}