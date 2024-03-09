package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.exception.RequestValidationException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

class ItemRequestServiceTest {
    private static ItemRequestService itemRequestService;
    private static ItemRequestRepository itemRequestRepository;
    private static UserService userService;

    private static User testUserBooker;

    private static ItemRequest testItemRequestNoDescription;

    @BeforeAll
    static void init() {
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        userService = Mockito.mock(UserService.class);

        itemRequestService = new ItemRequestService(userService, itemRequestRepository);

        testUserBooker = User.builder()
                .id(2L)
                .name("bookerUser")
                .email("booker@user.com")
                .build();

        testItemRequestNoDescription = ItemRequest.builder()
                .created(LocalDateTime.now())
                .authorId(testUserBooker.getId())
                .build();

    }

    @Test
    void testAddNewFailedWithWrongDescription() {
        Exception e = Assertions.assertThrows(RequestValidationException.class, () -> {
            itemRequestService.addNew(testItemRequestNoDescription, testUserBooker.getId());
        });

        Assertions.assertEquals("Текст заявки не может быть пустым", e.getMessage());

    }

    @Test
    void testGetAllPaginatedFailedWithWrongPaginationParams() {
        Exception e = Assertions.assertThrows(RequestValidationException.class, () -> {
            itemRequestService.getAllPaginated(-1, -1);
        });

        Assertions.assertEquals("Некорректные параметры запроса с постраничным выводом", e.getMessage());

    }

    @Test
    void testGetItemRequestByIdFailedWithRequestIdNotExisting() {
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Long requestId = 888L;
        Exception e = Assertions.assertThrows(RecordNotFoundException.class, () -> {
            itemRequestService.getItemRequestById(requestId);
        });

        Assertions.assertEquals("Заявка с id " + requestId + " не найдена", e.getMessage());

    }

}