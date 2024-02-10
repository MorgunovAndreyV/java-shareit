package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.Objects;


@Data
@Builder
public class Item {
    Long id;
    String name;
    String description;
    Boolean available;
    User owner;

    public void fillFromDto(ItemDto itemDto) {
        if (itemDto.getName() != null) {
            this.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            this.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            this.setAvailable(itemDto.getAvailable());
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name) && Objects.equals(description, item.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}
