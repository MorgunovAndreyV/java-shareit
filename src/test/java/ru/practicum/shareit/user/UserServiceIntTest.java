package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.config.ContextConfig;
import ru.practicum.shareit.user.model.User;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({ContextConfig.class, UserService.class})
class UserServiceIntTest {

    private final UserService userService;

    private static User testUserOwner;
    private static User testUserBooker;

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

}