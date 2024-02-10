package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemValidationException;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.exception.UserStorageException;
import ru.practicum.shareit.item.dto.DTOMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {
    private Set<Item> items = new HashSet<>();
    @Qualifier("InMemoryUserStorage")
    private final UserStorage userStorage;

    @Override
    public Set<Item> getByOwner(Long id) {
        return items.stream().filter(item -> id.equals(item.getOwner().getId())).collect(Collectors.toSet());
    }

    @Override
    public Set<Item> getItemsByText(String text) {
        if (text == "") {
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
        itemCreateValidations(item);
        item.setOwner(userStorage.getUserById(id));

        if (items.contains(item)) {
            throw new UserStorageException("Такая вещь уже добавлена");
        }

        assignNewId(item);
        items.add(item);

        log.info("Новая вещь добавлена успешно. id:" + item.getId());

        return item;
    }

    @Override
    public Item change(Item item, Long id) {
        if (!userStorage.getUserById(id).equals(getItemById(item.getId()).getOwner())) {
            throw new RecordNotFoundException("Вещи для указанного владельца не существует");
        }

        Item itemFromBase = getItemById(item.getId());
        itemFromBase.fillFromDto(DTOMapper.getItemDto(item));

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

    private void itemCreateValidations(Item item) {
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ItemValidationException("Имя не может быть пустым");

        }

        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new ItemValidationException("Описание не может быть пустым");

        }

        if (item.getAvailable() == null) {
            throw new ItemValidationException("Доступность не может быть пустой");

        }

    }

}
