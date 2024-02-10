package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Objects;

@Data
@Builder
public class User {
    Long id;
    String email;
    String name;

    void fillFromDto(UserDto userDto) {
        if (userDto.getName() != null) {
            this.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            this.setEmail(userDto.getEmail());
        }

    }

    UserDto getDto() {
        return UserDto.builder()
                .id(this.getId())
                .name(this.getName())
                .email(this.getEmail())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email) && Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name);
    }
}
