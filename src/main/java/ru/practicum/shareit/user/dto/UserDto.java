package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
@Builder
public class UserDto {
    Long id;
    String email;
    String name;

    public UserDto(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public User getUser() {
        return User.builder()
                .id(this.id)
                .email(this.email)
                .name(this.name)
                .build();
    }

}
