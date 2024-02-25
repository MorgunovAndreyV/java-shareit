package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.exception.UserStorageException;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    public Set<User> getAll() {
        return userStorage.getAll();
    }

    public User addNew(User user) throws UserStorageException, UserValidationException {
        Set<User> userList = getAll();

        userCreateValidations(user, userList);
        validateEmail(user);

        User newUser = userStorage.addNew(user);
        log.info("Новый пользователь добавлен успешно. id:" + user.getId());

        return newUser;
    }

    public User getUserById(Long id) {
        User user = userStorage.getUserById(id);

        if (user == null) {
            throw new RecordNotFoundException("Пользователь с id " + id + " не найден");
        }

        return user;
    }

    public User change(User user) throws RecordNotFoundException, UserValidationException {
        if (user.getEmail() != null) {
            validateEmail(user);
        }

        User changedUser = userStorage.change(user);
        log.info("Запись пользователя изменена успешно. id:" + user.getId());

        return changedUser;
    }

    public void delete(Long id) throws RecordNotFoundException {
        User user = userStorage.getUserById(id);

        if (user == null) {
            throw new RecordNotFoundException("Пользователь с id " + id + " не найден");
        }

        userStorage.deleteUserById(id);

    }

    private void userCreateValidations(User user, Set<User> userList) {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new UserValidationException("Имя не может быть пустым");

        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new UserValidationException("Почта не может быть пустой");

        }

    }

    private void validateEmail(User user) {
        if (user.getEmail() != null) {
            if (!user.getEmail().contains("@")) {
                throw new UserValidationException("Некорректный формат почты");

            }

        }

    }

    List<User> getUserByEmail(String email, Set<User> userList) {
        return userList.stream()
                .filter(user -> email.equals(user.getEmail())).collect(Collectors.toList());
    }
}
