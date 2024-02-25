package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemStorageException;
import ru.practicum.shareit.exception.ItemValidationException;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    @Qualifier("ItemDbStorage")
    private final ItemStorage itemStorage;
    @Autowired
    private final UserService userService;

    public Set<Item> getByOwner(Long id) {
        return itemStorage.getByOwner(id);
    }

    public Item addNew(Item item, Long id) throws ItemStorageException {
        itemDataValidations(item);
        userService.getUserById(id);
        itemAlreadyExists(item, itemStorage.getAll());

        Item newItem = itemStorage.addNew(item, id);
        log.info("Новая вещь добавлена успешно. id:" + item.getId());

        return newItem;
    }

    public Item getItemById(Long id) throws RecordNotFoundException {
        Item item = itemStorage.getItemById(id);

        if (item == null) {
            throw new RecordNotFoundException("Вешь с id " + id + " не найдена");
        }

        return item;
    }

    public Set<Item> getAvailableItemsByText(String text) {
        return itemStorage.getItemsByText(text).stream()
                .filter(item -> item.getAvailable() == true).collect(Collectors.toSet());
    }

    public Item change(Item item, Long id) throws RecordNotFoundException, UserValidationException {
        if (!userService.getUserById(id).equals(getItemById(item.getId()).getOwner())) {
            throw new RecordNotFoundException("Вещи для указанного владельца не существует");
        }

        Item changedItem = itemStorage.change(item, id);
        log.info("Запись вещи изменена успешно. id:" + item.getId());

        return changedItem;
    }

    private void itemDataValidations(Item item) {
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

    private void itemAlreadyExists(Item item, Set<Item> itemList) {
        if (itemList.contains(item)) {
            throw new ItemValidationException("Такая вещь уже добавлена");
        }

    }

}
