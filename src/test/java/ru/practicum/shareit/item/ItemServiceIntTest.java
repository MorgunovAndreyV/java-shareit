package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.config.ContextConfig;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({ContextConfig.class, ItemService.class})
class ItemServiceIntTest {

    private final ItemService itemService;
    private final UserService userService;

    private static Item testItem;
    private static Item testItem2;
    private static Item testItem3;
    private static Item testItem4;
    private static Item testItem5;

    private static User testUserOwner;

    @BeforeAll
    static void init() {
        testUserOwner = User.builder()
                .name("user_owner1")
                .email("user_owner1@user.com")
                .build();

        testItem = Item.builder()
                .name("Test item")
                .description("Just test item")
                .available(true)
                .build();

        testItem2 = Item.builder()
                .name("Test item #2")
                .description("Just test item #2")
                .available(true)
                .build();

        testItem3 = Item.builder()
                .name("Test item #3")
                .description("Just test item #3")
                .available(true)
                .build();

        testItem4 = Item.builder()
                .name("Test item #4")
                .description("Just test item #4")
                .available(true)
                .build();

        testItem5 = Item.builder()
                .name("Test item #5")
                .description("Just test item #5")
                .available(true)
                .requestId(1L)
                .build();

    }

    @BeforeEach
    void prepareData() {
        if (testUserOwner.getId() == null) {
            testUserOwner = userService.addNew(testUserOwner);
        }

    }

    @Test
    void testAddNewSuccess() {
        Item newItem = itemService.addNew(testItem, testUserOwner.getId());

        Assertions.assertEquals(newItem.getName(), testItem.getName());
        Assertions.assertEquals(newItem.getDescription(), testItem.getDescription());
        Assertions.assertEquals(newItem.getOwner(), testUserOwner);
        Assertions.assertEquals(newItem.getAvailable(), testItem.getAvailable());

        Item itemFromDb = itemService.getItemById(newItem.getId());

        Assertions.assertEquals(itemFromDb.getName(), newItem.getName());
        Assertions.assertEquals(itemFromDb.getDescription(), newItem.getDescription());
        Assertions.assertEquals(itemFromDb.getOwner(), newItem.getOwner());
        Assertions.assertEquals(itemFromDb.getAvailable(), newItem.getAvailable());
        Assertions.assertEquals(itemFromDb.getId(), newItem.getId());

    }

    @Test
    void testChangeItemSuccess() {
        Item newItem = itemService.addNew(testItem2, testUserOwner.getId());

        Assertions.assertEquals(newItem.getName(), testItem2.getName());
        Assertions.assertEquals(newItem.getDescription(), testItem2.getDescription());
        Assertions.assertEquals(newItem.getOwner(), testUserOwner);
        Assertions.assertEquals(newItem.getAvailable(), testItem2.getAvailable());

        String newName = "new name of item #2";
        String newDescription = "new description of item #2";

        Item itemFromDb = itemService.change(Item.builder()
                .id(newItem.getId())
                .description(newDescription)
                .name(newName)
                .build(), testUserOwner.getId());

        Assertions.assertEquals(itemFromDb.getName(), newName);
        Assertions.assertEquals(itemFromDb.getDescription(), newDescription);
        Assertions.assertEquals(itemFromDb.getOwner(), newItem.getOwner());
        Assertions.assertEquals(itemFromDb.getAvailable(), newItem.getAvailable());
        Assertions.assertEquals(itemFromDb.getId(), newItem.getId());

    }

    @Test
    void testgetAvailableItemsByTextEmptyRequestedText() {
        List<Item> foundItems = itemService.getAvailableItemsByText("");

        Assertions.assertTrue(foundItems.isEmpty());

    }

    @Test
    void testgetAvailableItemsByText() {
        Item newItem = itemService.addNew(testItem3, testUserOwner.getId());

        List<Item> foundItemFromDb = itemService.getAvailableItemsByText("#3");

        Assertions.assertTrue(foundItemFromDb.contains(newItem));
    }

    @Test
    void testGetByOwner() {
        Item newItem = itemService.addNew(testItem4, testUserOwner.getId());

        List<Item> itemsFromDb = itemService.getByOwner(testUserOwner.getId());

        Assertions.assertTrue(itemsFromDb.contains(newItem));
    }

    @Test
    void testGetByRequestId() {
        Item newItem = itemService.addNew(testItem5, testUserOwner.getId());

        List<Item> itemsFromDb = itemService.getItemsByRequestId(newItem.getRequestId());

        Assertions.assertTrue(itemsFromDb.contains(newItem));
    }

}