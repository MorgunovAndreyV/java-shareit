package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.CommentValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class CommentServiceTest {
    private static CommentService commentService;
    private static CommentRepository commentRepository;
    private static BookingService bookingService;
    private static UserService userService;
    private static ItemService itemService;

    private static CommentDto commentDto1;
    private static CommentDto commentDto2;

    private static User testUserOwner;
    private static User testUserBooker;
    private static User testUserBooker2;

    private static Item testItem;

    private static Booking testBooking;

    private static List<Booking> bookingList;

    @BeforeAll
    static void init() {
        commentRepository = Mockito.mock(CommentRepository.class);
        bookingService = Mockito.mock(BookingService.class);
        userService = Mockito.mock(UserService.class);
        itemService = Mockito.mock(ItemService.class);
        commentService = new CommentService(commentRepository, bookingService,
                userService, itemService);

        commentDto1 = CommentDto.builder()
                .build();

        commentDto2 = CommentDto.builder()
                .text("Comment text")
                .build();

        testItem = Item.builder()
                .id(1L)
                .name("Test item")
                .description("Just test item")
                .available(true)
                .owner(testUserOwner)
                .build();

        testUserOwner = User.builder()
                .id(1L)
                .name("user_owner1")
                .email("user_owner1@user.com")
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

        testBooking = Booking.builder()
                .id(1L)
                .item(testItem)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .booker(testUserBooker2)
                .build();

        bookingList = new ArrayList<>();
        bookingList.add(testBooking);

    }

    @Test
    void testAddNewFailedByEmptyCommentText() {
        Mockito
                .when(itemService.getItemById(Mockito.anyLong()))
                .thenReturn(testItem);

        Mockito
                .when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(testUserBooker);

        Exception e = Assertions.assertThrows(CommentValidationException.class, () -> {
            commentService.addNew(testItem.getId(), testUserBooker.getId(), commentDto1);
        });

        Assertions.assertEquals("Текст комментария не может быть пустым", e.getMessage());

    }

    @Test
    void testAddNewFailedByUserNotBooker() {
        Mockito
                .when(itemService.getItemById(Mockito.anyLong()))
                .thenReturn(testItem);

        Mockito
                .when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(testUserBooker);

        Mockito
                .when(bookingService.getByBooker(Mockito.anyLong()))
                .thenReturn(new ArrayList<>());

        Exception e = Assertions.assertThrows(CommentValidationException.class, () -> {
            commentService.addNew(testItem.getId(), testUserBooker.getId(), commentDto2);

        });

        Assertions.assertEquals("Для размещения комментария не найдены бронирования пользователем " +
                testUserBooker.getId() + " вещи " + testItem.getId(), e.getMessage());

    }

}