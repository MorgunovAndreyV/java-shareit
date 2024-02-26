package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemStorageException;
import ru.practicum.shareit.exception.ItemValidationException;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    public List<Item> getByOwner(Long id) {
        return itemRepository.findByOwnerId(id);
    }

    public Item addNew(Item item, Long id) throws ItemStorageException {
        validateItemData(item);
        userService.getUserById(id);
        itemAlreadyExists(item, itemRepository.findAll());

        item.setOwner(userService.getUserById(id));

        Item newItem = itemRepository.save(item);
        log.info("Новая вещь добавлена успешно. id:" + item.getId());

        return newItem;
    }

    public Item getItemById(Long id) throws RecordNotFoundException {
        Item item = itemRepository.findById(id).orElse(null);

        if (item == null) {
            throw new RecordNotFoundException("Вещь с id " + id + " не найдена");
        }

        return item;
    }

    public List<Item> getAvailableItemsByText(String text) {
        if (Objects.equals(text, "")) {
            return new ArrayList<>();
        }

        return itemRepository.findByDescriptionContainingIgnoreCase(text).stream()
                .filter(Item::getAvailable).collect(Collectors.toList());
    }

    public Item change(Item item, Long id) throws RecordNotFoundException, UserValidationException {
        if (!userService.getUserById(id).equals(getItemById(item.getId()).getOwner())) {
            throw new RecordNotFoundException("Вещи для указанного владельца не существует");
        }

        Item itemFromBase = getItemById(item.getId());
        ItemMapper.fillFromDto(ItemMapper.toDto(item), itemFromBase);

        Item changedItem = itemRepository.save(itemFromBase);
        log.info("Запись вещи изменена успешно. id:" + item.getId());

        return changedItem;
    }

    private void validateItemData(Item item) {
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

    private void itemAlreadyExists(Item item, List<Item> itemList) {
        if (itemList.contains(item)) {
            throw new ItemValidationException("Такая вещь уже добавлена");
        }

    }

}
