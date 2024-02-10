package ru.practicum.shareit.user;

import java.util.Set;

public interface UserStorage {
    Set<User> getAll();

    User addNew(User user);

    User change(User user);

    User getUserById(Long id);

    void deleteUserById(Long id);
}
