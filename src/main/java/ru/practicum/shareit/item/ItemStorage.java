package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Set;

public interface ItemStorage {
    Set<Item> getAll();

    Set<Item> getByOwner(Long id);

    Set<Item> getItemsByText(String text);

    Item addNew(Item item, Long id);

    Item change(Item item, Long id);

    Item getItemById(Long id);
}
