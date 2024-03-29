package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import java.util.Set;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntTest {
    private static User testUserOwner;
    private static User testUserBooker;
    private static User testUserBooker2;
    private static User testUserBooker3;

    private final UserService userService;

    @BeforeAll
    static void init() {
        testUserOwner = User.builder()
                .name("user_owner1")
                .email("user_owner1@user.com")
                .build();

        testUserBooker = User.builder()
                .name("bookerUser")
                .email("booker@user.com")
                .build();

        testUserBooker2 = User.builder()
                .name("bookerUser2")
                .email("booker2@user.com")
                .build();

        testUserBooker3 = User.builder()
                .name("bookerUser3")
                .email("booker3@user.com")
                .build();

    }

    @Test
    void testAddNewSuccess() {
        User newUser = userService.addNew(testUserBooker);
        User newUserFromDb = userService.getUserById(newUser.getId());

        Assertions.assertEquals(newUser.getName(), testUserBooker.getName());
        Assertions.assertEquals(newUser.getEmail(), testUserBooker.getEmail());

        Assertions.assertEquals(newUserFromDb.getName(), newUser.getName());
        Assertions.assertEquals(newUserFromDb.getEmail(), newUser.getEmail());

    }

    @Test
    void testChangeSuccess() {
        User newUser = userService.addNew(testUserOwner);
        String newName = "new name for user-owner";
        String newEmail = "newmail@mail.ru";
        User userFromDb = userService.change(User.builder()
                .id(newUser.getId())
                .name(newName)
                .email(newEmail)
                .build());
        User newUserFromDb = userService.getUserById(newUser.getId());

        Assertions.assertEquals(userFromDb.getName(), newName);
        Assertions.assertEquals(userFromDb.getEmail(), newEmail);
        Assertions.assertEquals(userFromDb.getId(), newUser.getId());

        Assertions.assertEquals(newUserFromDb.getName(), newName);
        Assertions.assertEquals(newUserFromDb.getEmail(), newEmail);

    }

    @Test
    void testGetAllUsers() {
        User newUser1 = userService.addNew(testUserBooker2);
        User newUser2 = userService.addNew(testUserBooker3);
        Set<User> userList = userService.getAll();

        Assertions.assertTrue(userList.contains(newUser1));
        Assertions.assertTrue(userList.contains(newUser2));
    }

    @Test
    void testDeleteUser() {
        User newUser1 = userService.addNew(testUserBooker2);
        User newUser2 = userService.addNew(testUserBooker3);
        Set<User> userList = userService.getAll();

        Assertions.assertTrue(userList.contains(newUser1));
        Assertions.assertTrue(userList.contains(newUser2));

        userService.delete(newUser1.getId());
        userService.delete(newUser2.getId());

        userList = userService.getAll();

        Assertions.assertFalse(userList.contains(newUser1));
        Assertions.assertFalse(userList.contains(newUser2));

    }

}