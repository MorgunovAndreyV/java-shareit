package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

class ItemServiceTest {

    private static ItemService itemService;
    private static ItemRepository itemRepository;
    private static UserService userService;

    private static Item testItem;

    private static User testUserOwner;
    private static User testUserOwner2;

    @BeforeAll
    static void init() {
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

        testItem = Item.builder()
                .id(1L)
                .name("Test item")
                .description("Just test item")
                .available(true)
                .owner(testUserOwner)
                .build();

        itemRepository = Mockito.mock(ItemRepository.class);
        userService = Mockito.mock(UserService.class);

        itemService = new ItemService(itemRepository, userService);

    }

    @Test
    void testGetItemByIdFailedByIdNotFound() {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Long itemId = 888L;
        Exception e = Assertions.assertThrows(RecordNotFoundException.class, () -> {
            itemService.getItemById(itemId);
        });

        Assertions.assertEquals("Вещь с id " + itemId + " не найдена", e.getMessage());

    }

    @Test
    void testChangeFailedByOtherOwnerId() {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testItem));
        Mockito.when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(testUserOwner2);

        Exception e = Assertions.assertThrows(RecordNotFoundException.class, () -> {
            itemService.change(testItem, testUserOwner2.getId());
        });

        Assertions.assertEquals("Вещи для указанного владельца не существует", e.getMessage());

    }

}