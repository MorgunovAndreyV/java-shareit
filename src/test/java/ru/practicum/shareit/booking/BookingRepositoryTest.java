package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class BookingRepositoryTest {
    private static Item testItem;
    private static Item testItem4;
    private static Item testItem5;

    private static User testUserOwner;
    private static User testUserOwner2;
    private static User testUserBooker;
    private static User testUserBooker2;


    private static Booking testBooking;
    private static Booking testBooking2;
    private static Booking testBooking3;
    private static Booking testBooking4;
    private static Booking testBooking5;

    @Autowired
    private TestEntityManager em;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;


    @BeforeEach
    void createData() {
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
                .owner(testUserOwner)
                .build();

        testItem4 = Item.builder()
                .name("Test item 4")
                .description("Just test item 4")
                .available(true)
                .owner(testUserOwner2)
                .build();

        testItem5 = Item.builder()
                .name("Test item 5")
                .description("Just test item 5")
                .available(true)
                .owner(testUserOwner)
                .build();

        testBooking = Booking.builder()
                .item(testItem)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .booker(testUserBooker)
                .build();

        testBooking2 = Booking.builder()
                .item(testItem)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .booker(testUserBooker)
                .build();

        testBooking3 = Booking.builder()
                .item(testItem)
                .status(BookingStatus.REJECTED)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .booker(testUserBooker)
                .build();

        testBooking4 = Booking.builder()
                .item(testItem4)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .booker(testUserBooker)
                .build();

        testBooking5 = Booking.builder()
                .item(testItem5)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .booker(testUserBooker2)
                .build();

        testUserBooker = userRepository.save(testUserBooker);
        testUserBooker2 = userRepository.save(testUserBooker2);
        testUserOwner = userRepository.save(testUserOwner);
        testUserOwner2 = userRepository.save(testUserOwner2);
        testItem.setOwner(testUserOwner);
        testItem = itemRepository.save(testItem);
        testItem4.setOwner(testUserOwner2);
        testItem4 = itemRepository.save(testItem4);
        testItem5 = itemRepository.save(testItem5);
        testBooking = bookingRepository.save(testBooking);
        testBooking2 = bookingRepository.save(testBooking2);
        testBooking3 = bookingRepository.save(testBooking3);
        testBooking4 = bookingRepository.save(testBooking4);
        testBooking5 = bookingRepository.save(testBooking5);

    }

    @AfterEach
    public void tearDown() {
        bookingRepository.delete(testBooking5);
        bookingRepository.delete(testBooking4);
        bookingRepository.delete(testBooking);
        bookingRepository.delete(testBooking2);
        bookingRepository.delete(testBooking3);

        itemRepository.delete(testItem);
        itemRepository.delete(testItem4);
        itemRepository.delete(testItem5);

        userRepository.delete(testUserBooker);
        userRepository.delete(testUserBooker2);
        userRepository.delete(testUserOwner);
        userRepository.delete(testUserOwner2);

    }

    @Test
    void testFindByBookerId() {
        List<Booking> bookingList = bookingRepository.findByBookerId(testUserBooker.getId());

        Assertions.assertTrue(bookingList.contains(testBooking));
        Assertions.assertTrue(bookingList.contains(testBooking2));
        Assertions.assertTrue(bookingList.contains(testBooking3));
        Assertions.assertTrue(bookingList.contains(testBooking4));

    }

    @Test
    void testFindByItemOwnerId() {
        List<Booking> bookingList = bookingRepository.findByItemOwnerId(testUserOwner.getId());

        Assertions.assertTrue(bookingList.contains(testBooking));
        Assertions.assertTrue(bookingList.contains(testBooking2));
        Assertions.assertTrue(bookingList.contains(testBooking3));
        Assertions.assertFalse(bookingList.contains(testBooking4));

    }

    @Test
    void testFindPastBookingsForBooker() {
        List<Booking> bookingList = bookingRepository.findPastBookingsForBooker(testUserBooker.getId());

        Assertions.assertTrue(bookingList.contains(testBooking3));

    }

    @Test
    void testFindPastBookingsForOwner() {
        List<Booking> bookingList = bookingRepository.findPastBookingsForOwner(testUserOwner.getId());

        Assertions.assertTrue(bookingList.contains(testBooking3));
        Assertions.assertEquals(1, bookingList.size());

    }

    @Test
    void testFindCurrentBookingsForBooker() {
        List<Booking> bookingList = bookingRepository.findCurrentBookingsForBooker(testUserBooker.getId());

        Assertions.assertTrue(bookingList.contains(testBooking2));

    }

    @Test
    void testFindCurrentBookingsForOwner() {
        List<Booking> bookingList = bookingRepository.findCurrentBookingsForOwner(testUserOwner.getId());

        Assertions.assertTrue(bookingList.contains(testBooking2));

    }

    @Test
    void testFindFutureBookingsForBooker() {
        List<Booking> bookingList = bookingRepository.findFutureBookingsForBooker(testUserBooker.getId());

        Assertions.assertTrue(bookingList.contains(testBooking));
        Assertions.assertTrue(bookingList.contains(testBooking4));

    }

    @Test
    void testFindFutureBookingsForOwner() {
        List<Booking> bookingList = bookingRepository.findFutureBookingsForOwner(testUserOwner.getId());

        Assertions.assertTrue(bookingList.contains(testBooking5));

    }

    @Test
    void testFindWaitingBookingsForBooker() {
        List<Booking> bookingList = bookingRepository.findWaitingBookingsForBooker(testUserBooker.getId());

        Assertions.assertTrue(bookingList.contains(testBooking));
        Assertions.assertTrue(bookingList.contains(testBooking4));

    }

    @Test
    void testFindWaitingBookingsForOwner() {
        List<Booking> bookingList = bookingRepository.findFutureBookingsForOwner(testUserOwner.getId());

        Assertions.assertTrue(bookingList.contains(testBooking));
        Assertions.assertTrue(bookingList.contains(testBooking5));

    }

    @Test
    void testFindRejectedBookingsForBooker() {
        List<Booking> bookingList = bookingRepository.findRejectedBookingsForBooker(testUserBooker.getId());

        Assertions.assertTrue(bookingList.contains(testBooking3));

    }

    @Test
    void testFindRejectedBookingsForOwner() {
        List<Booking> bookingList = bookingRepository.findRejectedBookingsForOwner(testUserOwner.getId());

        Assertions.assertTrue(bookingList.contains(testBooking3));

    }

    @Test
    void findLastBookingIdForItem() {
        Booking bookingFound = bookingRepository.findLastBookingIdForItem(testItem.getId());

        Assertions.assertTrue(testBooking2.equals(bookingFound));

    }

    @Test
    void findNextBookingForItem() {
        Booking bookingFound = bookingRepository.findNextBookingForItem(testItem.getId());

        Assertions.assertTrue(testBooking.equals(bookingFound));

    }
}
