package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component("ItemDbStorage")
@RequiredArgsConstructor
public class ItemDbStorage implements ItemStorage {
    private final ItemRepository itemRepository;
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    @Override
    public Set<Item> getAll() {
        List<Item> allItems = itemRepository.findAll();
        return new HashSet<>(allItems);
    }

    @Override
    public Set<Item> getByOwner(Long id) {
        return new HashSet<>(itemRepository.findByOwnerId(id));
    }

    @Override
    public Set<Item> getItemsByText(String text) {
        if (Objects.equals(text, "")) {
            return new HashSet<Item>();
        }

        Set<Item> items = new HashSet<>(itemRepository.findByNameContainingIgnoreCase(text));
        items.addAll(itemRepository.findByDescriptionContainingIgnoreCase(text));

        return items;
    }

    @Override
    public Item addNew(Item item, Long id) {
        item.setOwner(userStorage.getUserById(id));

        return itemRepository.save(item);
    }

    @Override
    public Item change(Item item, Long id) {
        Item itemFromBase = getItemById(item.getId());
        ItemMapper.fillFromDto(ItemMapper.toDto(item), itemFromBase);

        return itemRepository.save(itemFromBase);
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElse(null);
    }

}
