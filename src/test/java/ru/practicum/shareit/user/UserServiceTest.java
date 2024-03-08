package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

class UserServiceTest {

    private static UserService userService;
    private static UserRepository userRepository;

    private static User testUserMailEmpty;
    private static User testUserNameEmpty;
    private static User testUserMailWrong;

    @BeforeAll
    static void init() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);

        testUserNameEmpty = User.builder()
                .id(4L)
                .email("booker@user.com")
                .build();

        testUserMailEmpty = User.builder()
                .id(5L)
                .name("bookerUser2")
                .build();

        testUserMailWrong = User.builder()
                .id(5L)
                .name("bookerUser2")
                .email("bookeruser.com")
                .build();

    }

    @Test
    void testAddNewFailedWithNameEmpty() {
        Exception e = Assertions.assertThrows(UserValidationException.class, () -> {
            userService.addNew(testUserNameEmpty);
        });

        Assertions.assertEquals("Имя не может быть пустым", e.getMessage());

    }

    @Test
    void testAddNewFailedWithMailEmpty() {
        Exception e = Assertions.assertThrows(UserValidationException.class, () -> {
            userService.addNew(testUserMailEmpty);
        });

        Assertions.assertEquals("Почта не может быть пустой", e.getMessage());

    }

    @Test
    void testAddNewFailedWithMailWrongFormat() {
        Exception e = Assertions.assertThrows(UserValidationException.class, () -> {
            userService.addNew(testUserMailWrong);
        });

        Assertions.assertEquals("Некорректный формат почты", e.getMessage());

    }

    @Test
    void testGetUserByIdFailedWithUserNotExists() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(RecordNotFoundException.class, () -> {
            userService.getUserById(testUserNameEmpty.getId());
        });

        Assertions.assertEquals("Пользователь с id " + testUserNameEmpty.getId()
                + " не найден", e.getMessage());

    }

}