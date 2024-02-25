package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component("InMemoryItemStorage")
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private Set<Item> items = new HashSet<>();

    @Qualifier("InMemoryUserStorage")
    private final UserStorage userStorage;


    @Override
    public Set<Item> getAll() {
        return items;
    }

    @Override
    public Set<Item> getByOwner(Long id) {
        return items.stream().filter(item -> id.equals(item.getOwner().getId())).collect(Collectors.toSet());
    }

    @Override
    public Set<Item> getItemsByText(String text) {
        if (Objects.equals(text, "")) {
            return new HashSet<Item>();
        }

        Set<Item> foundItems = items.stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toSet());

        return foundItems;
    }

    @Override
    public Item addNew(Item item, Long id) {
        item.setOwner(userStorage.getUserById(id));
        assignNewId(item);
        items.add(item);

        return item;
    }

    @Override
    public Item change(Item item, Long id) {
        Item itemFromBase = getItemById(item.getId());
        ItemMapper.fillFromDto(ItemMapper.toDto(item), itemFromBase);

        return itemFromBase;
    }

    @Override
    public Item getItemById(Long id) {
        Optional<Item> possibleItem = items.stream()
                .filter(item1 -> item1.getId().equals(id))
                .findFirst();

        if (possibleItem.isEmpty()) {
            throw new RecordNotFoundException("Вещи с ID " + id + " не найдено.");

        }

        return possibleItem.get();
    }

    private void assignNewId(Item item) {
        if (items != null) {
            item.setId((long) (items.size() + 1));
        }

    }

}
