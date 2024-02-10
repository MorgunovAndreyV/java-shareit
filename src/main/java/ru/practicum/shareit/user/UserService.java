package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.exception.UserStorageException;
import ru.practicum.shareit.exception.UserValidationException;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("InMemoryUserStorage")
    private final UserStorage userStorage;

    public Set<User> getAll() {
        return userStorage.getAll();
    }

    public User addNew(User user) throws UserStorageException, UserValidationException {
        return userStorage.addNew(user);
    }

    public User getUserById(Long id) throws RecordNotFoundException {
        return userStorage.getUserById(id);
    }

    public User change(User user) throws RecordNotFoundException, UserValidationException {
        return userStorage.change(user);
    }

    public void delete(Long id) throws RecordNotFoundException {
        userStorage.deleteUserById(id);

    }
}
