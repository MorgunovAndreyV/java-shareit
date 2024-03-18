package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ItemControllerBadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getByOwner(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByText(@RequestParam("text") String text) {
        log.info("searchByText text" + text);

        return itemClient.getByText(text);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable("id") Long itemId,
                                                @RequestBody CommentDto commentDto) {
        return itemClient.createComment(userId, itemId, commentDto);
    }

    @PostMapping
    public ResponseEntity<Object> addNew(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        validateItemData(itemDto);

        return itemClient.addNew(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> change(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("id") Long itemId,
                                         @RequestBody ItemDto itemDto) {
        return itemClient.change(userId, itemId, itemDto);
    }

    private void validateItemData(ItemDto item) {
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ItemControllerBadRequestException("Имя не может быть пустым");

        }

        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new ItemControllerBadRequestException("Описание не может быть пустым");

        }

        if (item.getAvailable() == null) {
            throw new ItemControllerBadRequestException("Доступность не может быть пустой");

        }

    }

}