package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentServiceIntTest {
    private static CommentDto testCommentDto1;

    private static User testUserOwner;
    private static User testUserBooker;

    private static Item testItem;

    private static BookingDto testBookingDto1;
    private static Booking testBooking;

    private final CommentService commentService;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @BeforeAll
    static void init() {
        testCommentDto1 = CommentDto.builder()
                .text("Comment text")
                .build();

        testItem = Item.builder()
                .name("Test item")
                .description("Just test item")
                .available(true)
                .build();

        testUserBooker = User.builder()
                .name("bookerUser")
                .email("booker@user.com")
                .build();

        testUserOwner = User.builder()
                .name("user_owner1")
                .email("user_owner1@user.com")
                .build();

        testBookingDto1 = BookingDto.builder()
                .build();

        testBooking = Booking.builder()
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

        if (testItem.getId() == null) {
            testItem = itemService.addNew(testItem, testUserOwner.getId());
        }

        if (testBooking.getId() == null) {
            testBookingDto1.setItemId(testItem.getId());
            testBookingDto1.setStart(LocalDateTime.now().plusSeconds(4));
            testBookingDto1.setEnd(LocalDateTime.now().plusDays(2));
            Long bookingId1 = bookingService.addNew(testBookingDto1, testUserBooker.getId()).getId();
            testBooking = bookingService.getBookingById(bookingId1);
        }

    }

    @Test
    void testAddNewComment() {
        if (testBooking.getId() == null) {
            testBookingDto1.setItemId(testItem.getId());
            testBookingDto1.setStart(LocalDateTime.now().plusSeconds(4));
            testBookingDto1.setEnd(LocalDateTime.now().plusDays(2));
            Long bookingId1 = bookingService.addNew(testBookingDto1, testUserBooker.getId()).getId();
            testBooking = bookingService.getBookingById(bookingId1);
        }

        try {
            TimeUnit.SECONDS.sleep(4);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        }

        Comment newComment = commentService.addNew(testItem.getId(), testUserBooker.getId(), testCommentDto1);

        List<Comment> commentFromDb = commentService.getByItemId(testItem.getId());

        Set<Comment> commentFromDbSet = new HashSet<>(commentFromDb);

        Assertions.assertTrue(commentFromDbSet.contains(newComment));
        Assertions.assertTrue(commentFromDb.contains(newComment));

        Assertions.assertEquals(testCommentDto1.getText(), newComment.getText());
        Assertions.assertNotEquals(newComment.getCreated(), null);
        Assertions.assertNotEquals(newComment.getId(), null);
        Assertions.assertEquals(newComment.getAuthor().getId(), testUserBooker.getId());
        Assertions.assertEquals(newComment.getItem().getId(), testItem.getId());


    }

}