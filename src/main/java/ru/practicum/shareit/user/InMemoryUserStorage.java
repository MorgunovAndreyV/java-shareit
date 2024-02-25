package ru.practicum.shareit.user;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component("InMemoryUserStorage")
@Primary
public class InMemoryUserStorage implements UserStorage {
    private Set<User> users = new HashSet<>();
    private Long lastRecordId = 0L;

    @Override
    public Set<User> getAll() {
        return users;
    }

    @Override
    public User addNew(User user) {
        assignNewId(user);
        users.add(user);

        return user;
    }

    @Override
    public User change(User user) {
        User userFromBase = getUserById(user.getId());
        UserMapper.fillFromDto(UserMapper.toDto(user), userFromBase);

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

}
