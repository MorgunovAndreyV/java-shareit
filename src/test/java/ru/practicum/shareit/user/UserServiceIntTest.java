package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.config.ContextConfig;
import ru.practicum.shareit.user.model.User;

import java.util.Set;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({ContextConfig.class, UserService.class})
class UserServiceIntTest {

    private final UserService userService;

    private static User testUserOwner;
    private static User testUserBooker;
    private static User testUserBooker2;
    private static User testUserBooker3;

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

        Assertions.assertEquals(newUser.getName(), testUserBooker.getName());
        Assertions.assertEquals(newUser.getEmail(), testUserBooker.getEmail());

        User newUserFromDb = userService.getUserById(newUser.getId());

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

        Assertions.assertEquals(userFromDb.getName(), newName);
        Assertions.assertEquals(userFromDb.getEmail(), newEmail);
        Assertions.assertEquals(userFromDb.getId(), newUser.getId());

        User newUserFromDb = userService.getUserById(newUser.getId());

        Assertions.assertEquals(newUserFromDb.getName(), newName);
        Assertions.assertEquals(newUserFromDb.getEmail(), newEmail);

    }

    @Test
    void testGetAllUsers() {
        User newUser1 = userService.addNew(testUserBooker2);
        User newUser2 = userService.addNew(testUserBooker2);

        Set<User> userList = userService.getAll();

        Assertions.assertTrue(userList.contains(newUser1));
        Assertions.assertTrue(userList.contains(newUser2));
    }

}