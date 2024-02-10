package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comparators.ItemComparators;
import ru.practicum.shareit.item.dto.DTOMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getByOwner(@RequestHeader("X-Sharer-User-Id") Long id) {
        List<Item> sortedItemList = new ArrayList<>(itemService.getByOwner(id));
        sortedItemList.sort(ItemComparators.compareItemsById);

        return sortedItemList.stream().map(DTOMapper::getItemDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable Long id) {

        return DTOMapper.getItemDto(itemService.getItemById(id));
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam("text") String text) {
        List<Item> sortedItemList = new ArrayList<>(itemService.getAvailableItemsByText(text));
        sortedItemList.sort(ItemComparators.compareItemsById);

        return sortedItemList.stream().map(DTOMapper::getItemDto).collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto addNew(@RequestHeader("X-Sharer-User-Id") Long id, @RequestBody ItemDto item) {

        return DTOMapper.getItemDto(itemService.addNew(DTOMapper.getItemFromDto(item), id));
    }

    @PatchMapping("/{id}")
    public ItemDto change(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("id") Long itemId,
                          @RequestBody ItemDto item) {
        item.setId(itemId);

        return DTOMapper.getItemDto(itemService.change(DTOMapper.getItemFromDto(item), userId));
    }

}
