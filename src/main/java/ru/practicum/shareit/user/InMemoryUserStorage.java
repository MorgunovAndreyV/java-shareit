package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.exception.StoredDataConflict;
import ru.practicum.shareit.exception.UserStorageException;
import ru.practicum.shareit.exception.UserValidationException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private Set<User> users = new HashSet<>();
    private Long lastRecordId = 0L;

    @Override
    public Set<User> getAll() {
        return users;
    }

    @Override
    public User addNew(User user) {
        userCreateValidations(user);

        if (users.contains(user)) {
            throw new UserStorageException("Такой пользователь уже добавлен");
        }

        assignNewId(user);
        users.add(user);

        log.info("Новый пользователь добавлен успешно. id:" + user.getId());

        return user;
    }

    @Override
    public User change(User user) {
        User userFromBase = getUserById(user.getId());

        if (user.getEmail() != null) {
            validateEmail(user, users);
        }

        userFromBase.fillFromDto(user.getDto());

        log.info("Запись пользователя изменена успешно. id:" + user.getId());

        return userFromBase;
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> possibleUser = users.stream()
                .filter(user1 -> user1.getId().equals(id))
                .findFirst();

        if (possibleUser.isEmpty()) {
            throw new RecordNotFoundException("Пользователь с ID " + id + " не найден.");

        }

        return possibleUser.get();
    }

    @Override
    public void deleteUserById(Long id) {
        Optional<User> possibleUser =
                users.stream().filter(user -> id.equals(user.getId())).findFirst();
        if (possibleUser.isPresent()) {
            users.remove(possibleUser.get());
        } else {
            throw new RecordNotFoundException("Пользователь с id " + id + " не найден. Удаление не выполнено");
        }

    }

    private void assignNewId(User user) {
        lastRecordId++;
        user.setId(lastRecordId);

    }

    private void userCreateValidations(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new UserValidationException("Имя не может быть пустым");

        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new UserValidationException("Почта не может быть пустой");

        }

        validateEmail(user, users);

    }

    private static void validateEmail(User user, Set<User> userList) {
        if (user.getEmail() != null) {
            if (!user.getEmail().contains("@")) {
                throw new UserValidationException("Некорректный формат почты");

            }

        }

        List<User> usersWithThisEmail = getUserByEmail(user.getEmail(), userList).stream()
                .filter(user1 -> !user1.getId().equals(user.getId())).collect(Collectors.toList());

        if (!usersWithThisEmail.isEmpty()) {
            throw new StoredDataConflict("Почта уже используется другим пользователем");
        }

    }

    static List<User> getUserByEmail(String email, Set<User> userList) {
        return userList.stream()
                .filter(user -> email.equals(user.getEmail())).collect(Collectors.toList());
    }

}
