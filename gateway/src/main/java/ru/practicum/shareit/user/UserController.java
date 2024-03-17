package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.UserControllerBadRequestException;
import ru.practicum.shareit.user.dto.UserDto;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> addNew(@RequestBody UserDto user) {
        validateUserData(user);
        validateEmail(user);

        return userClient.addNew(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> change(@PathVariable Long id, @RequestBody UserDto user) {
        if (user.getEmail() != null) {
            validateEmail(user);
        }

        return userClient.change(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userClient.delete(id);
    }

    private void validateUserData(UserDto user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new UserControllerBadRequestException("Имя не может быть пустым");

        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new UserControllerBadRequestException("Почта не может быть пустой");

        }

    }

    private void validateEmail(UserDto user) {
        if (user.getEmail() != null) {
            if (!user.getEmail().contains("@")) {
                throw new UserControllerBadRequestException("Некорректный формат почты");

            }

        }

    }

}
