package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comparators.UserComparators;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        List<User> sortedUserList = new ArrayList<>(userService.getAll());
        sortedUserList.sort(UserComparators.compareUsersById);

        return sortedUserList.stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return UserMapper.toDto(userService.getUserById(id));
    }

    @PostMapping
    public UserDto addNew(@RequestBody UserDto user) {
        return UserMapper.toDto(userService.addNew(UserMapper.toEntity(user)));
    }

    @PatchMapping("/{id}")
    public UserDto change(@PathVariable Long id, @RequestBody UserDto user) {
        user.setId(id);
        return UserMapper.toDto(userService.change(UserMapper.toEntity(user)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

}
