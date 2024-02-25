package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashSet;
import java.util.Set;

@Component("UserDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final UserRepository userRepository;

    @Override
    public Set<User> getAll() {
        return new HashSet<>(userRepository.findAll());
    }

    @Override
    public User addNew(User user) {
        return userRepository.saveAndFlush(user);
    }

    @Override
    public User change(User user) {
        User userFromBase = getUserById(user.getId());
        UserMapper.fillFromDto(UserMapper.toDto(user), userFromBase);

        return userRepository.saveAndFlush(userFromBase);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

}
