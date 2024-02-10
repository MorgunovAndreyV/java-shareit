package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemStorageException;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    @Qualifier("InMemoryItemStorage")
    private final ItemStorage itemStorage;

    public Set<Item> getByOwner(Long id) {
        return itemStorage.getByOwner(id);
    }

    public Item addNew(Item item, Long id) throws ItemStorageException {
        return itemStorage.addNew(item, id);
    }

    public Item getItemById(Long id) throws RecordNotFoundException {
        return itemStorage.getItemById(id);
    }

    public Set<Item> getAvailableItemsByText(String text) {
        return itemStorage.getItemsByText(text).stream()
                .filter(item -> item.getAvailable() == true).collect(Collectors.toSet());
    }

    public Item change(Item item, Long id) throws RecordNotFoundException, UserValidationException {
        return itemStorage.change(item, id);
    }

}
