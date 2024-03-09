package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.config.ContextConfig;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({ContextConfig.class, ItemRequestService.class})
class ItemRequestServiceIntTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    private static User testUserBooker;
    private static User testUserBooker2;

    private static ItemRequest testItemRequest1;
    private static ItemRequest testItemRequest2;
    private static ItemRequest testItemRequest3;
    private static ItemRequest testItemRequest4;
    private static ItemRequest testItemRequest5;

    @BeforeAll
    static void init() {
        testUserBooker = User.builder()
                .name("bookerUser")
                .email("booker@user.com")
                .build();

        testUserBooker2 = User.builder()
                .name("bookerUser2")
                .email("booker2@user.com")
                .build();

        testItemRequest1 = ItemRequest.builder()
                .description("Test Description #1")
                .build();

        testItemRequest2 = ItemRequest.builder()
                .description("Test Description #2")
                .build();

        testItemRequest3 = ItemRequest.builder()
                .description("Test Description #3")
                .build();

        testItemRequest4 = ItemRequest.builder()
                .description("Test Description #4")
                .build();

        testItemRequest5 = ItemRequest.builder()
                .description("Test Description #5")
                .build();
    }

    @BeforeEach
    void prepareData() {
        if (testUserBooker.getId() == null) {
            testUserBooker = userService.addNew(testUserBooker);
        }

        if (testUserBooker2.getId() == null) {
            testUserBooker2 = userService.addNew(testUserBooker2);
        }

    }

    @Test
    void testAddSuccess() {
        ItemRequest request = itemRequestService.addNew(testItemRequest1, testUserBooker.getId());

        Assertions.assertEquals(request.getAuthorId(), testUserBooker.getId());
        Assertions.assertEquals(request.getDescription(), testItemRequest1.getDescription());
        Assertions.assertNotEquals(request.getCreated(), null);

    }

    @Test
    void testGetByAuthorSuccess() {
        ItemRequest request1 = itemRequestService.addNew(testItemRequest2, testUserBooker2.getId());
        ItemRequest request2 = itemRequestService.addNew(testItemRequest3, testUserBooker2.getId());
        ItemRequest request3 = itemRequestService.addNew(testItemRequest4, testUserBooker2.getId());

        Assertions.assertEquals(request1.getAuthorId(), testUserBooker2.getId());
        Assertions.assertEquals(request1.getDescription(), testItemRequest2.getDescription());
        Assertions.assertNotEquals(request1.getCreated(), null);

        Assertions.assertEquals(request2.getAuthorId(), testUserBooker2.getId());
        Assertions.assertEquals(request2.getDescription(), testItemRequest3.getDescription());
        Assertions.assertNotEquals(request2.getCreated(), null);

        Assertions.assertEquals(request3.getAuthorId(), testUserBooker2.getId());
        Assertions.assertEquals(request3.getDescription(), testItemRequest4.getDescription());
        Assertions.assertNotEquals(request3.getCreated(), null);

        List<ItemRequest> requestsByAuthor = itemRequestService.getByAuthor(testUserBooker2.getId());

        Assertions.assertTrue(requestsByAuthor.contains(request1));
        Assertions.assertTrue(requestsByAuthor.contains(request2));
        Assertions.assertTrue(requestsByAuthor.contains(request3));

    }

    @Test
    void testGetById() {
        ItemRequest request1 = testItemRequest2;

        if (testItemRequest2.getId() == null) {
            request1 = itemRequestService.addNew(testItemRequest2, testUserBooker2.getId());
        }

        ItemRequest requestFromDb = itemRequestService.getItemRequestById(request1.getId());

        Assertions.assertEquals(request1, requestFromDb);

    }

    @Test
    void testGetRequestPaginated() {
        ItemRequest itemRequest = itemRequestService.addNew(testItemRequest5, testUserBooker.getId());

        List<ItemRequest> itemRequestList = itemRequestService.getAllPaginated(null, null);

        Assertions.assertFalse(itemRequestList.isEmpty());
        Assertions.assertTrue(itemRequestList.contains(itemRequest));

        Integer itemRequestListSize = itemRequestList.size();

        List<ItemRequest> itemRequestListPaginated = itemRequestService.getAllPaginated(0, itemRequestListSize);

        Assertions.assertFalse(itemRequestListPaginated.isEmpty());
        Assertions.assertTrue(itemRequestListPaginated.contains(itemRequest));

    }

}