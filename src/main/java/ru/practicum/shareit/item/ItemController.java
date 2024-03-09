package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.comparators.CommentDtoComparators;
import ru.practicum.shareit.comparators.ItemComparators;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
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
    private final BookingService bookingService;
    private final CommentService commentService;

    @GetMapping
    public List<ItemDto> getByOwner(@RequestHeader("X-Sharer-User-Id") Long id) {
        List<Item> sortedItemList = new ArrayList<>(itemService.getByOwner(id));
        sortedItemList.sort(ItemComparators.compareItemsById);

        return sortedItemList.stream()
                .map(item -> {
                    ItemDto itemDto = ItemMapper.toDto(item);
                    bookingService.setLastBooking(itemDto);
                    bookingService.setNextBooking(itemDto);
                    itemDto.setComments(commentService.getByItemId(item.getId()).stream()
                            .map(CommentMapper::toDto).collect(Collectors.toList()));

                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable("id") Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = itemService.getItemById(itemId);
        ItemDto itemDto = ItemMapper.toDto(item);

        if (item.getOwner().getId().equals(userId)) {
            bookingService.setLastBooking(itemDto);
            bookingService.setNextBooking(itemDto);
        }

        List<CommentDto> commentDtos = commentService.getByItemId(item.getId()).stream()
                        .map(CommentMapper::toDto).collect(Collectors.toList());
        commentDtos.sort(CommentDtoComparators.compareCommentDtosById);

        itemDto.setComments(commentDtos);


        return itemDto;
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam("text") String text) {
        List<Item> sortedItemList = new ArrayList<>(itemService.getAvailableItemsByText(text));
        sortedItemList.sort(ItemComparators.compareItemsById);

        return sortedItemList.stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @PostMapping("/{id}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("id") Long itemId,
                                    @RequestBody CommentDto comment) {

        return CommentMapper.toDto(commentService.addNew(itemId, userId, comment));
    }

    @PostMapping
    public ItemDto addNew(@RequestHeader("X-Sharer-User-Id") Long id, @RequestBody ItemDto item) {
        return ItemMapper.toDto(itemService.addNew(ItemMapper.toEntity(item), id));
    }

    @PatchMapping("/{id}")
    public ItemDto change(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("id") Long itemId,
                          @RequestBody ItemDto item) {
        item.setId(itemId);

        return ItemMapper.toDto(itemService.change(ItemMapper.toEntity(item), userId));
    }

}
