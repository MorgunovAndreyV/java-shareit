package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Set<User> getAll() {
        return new HashSet<>(userRepository.findAll());
    }

    public User addNew(User user) throws UserValidationException {
        User newUser = userRepository.save(user);
        log.info("Новый пользователь добавлен успешно. id:" + user.getId());

        return newUser;
    }

    public User getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new RecordNotFoundException("Пользователь с id " + id + " не найден");
        }

        return user;
    }

    public User change(User user) throws RecordNotFoundException, UserValidationException {
        User userFromBase = getUserById(user.getId());
        UserMapper.fillFromDto(UserMapper.toDto(user), userFromBase);

        log.info("Запись пользователя изменена успешно. id:" + user.getId());

        return userRepository.save(userFromBase);
    }

    public void delete(Long id) throws RecordNotFoundException {
        getUserById(id);
        userRepository.deleteById(id);

    }


}
