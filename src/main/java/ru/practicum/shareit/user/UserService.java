package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.exception.UserStorageException;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("InMemoryUserStorage")
    private final UserStorage userStorage;

    public Set<User> getAll() {
        return userStorage.getAll();
    }

    public User addNew(User user) throws UserStorageException, UserValidationException {
        User newUser = userStorage.addNew(user);
        log.info("Новый пользователь добавлен успешно. id:" + user.getId());

        return newUser;
    }

    public User getUserById(Long id) throws RecordNotFoundException {
        return userStorage.getUserById(id);
    }

    public User change(User user) throws RecordNotFoundException, UserValidationException {
        User changedUser = userStorage.change(user);
        log.info("Запись пользователя изменена успешно. id:" + user.getId());

        return changedUser;
    }

    public void delete(Long id) throws RecordNotFoundException {
        userStorage.deleteUserById(id);

    }
}
