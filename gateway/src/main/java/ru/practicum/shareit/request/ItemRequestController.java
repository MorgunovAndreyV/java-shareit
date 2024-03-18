package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.RequestControllerBadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") Long requestId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getRequest(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getByAuthor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getByAuthor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllPaginated(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @Nullable @RequestParam("from") Integer numberFrom,
                                                  @Nullable @RequestParam("size") Integer pageSize) {
        validatePagination(numberFrom, pageSize);

        return itemRequestClient.getAll(userId, numberFrom, pageSize);
    }

    @PostMapping
    public ResponseEntity<Object> addNew(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody ItemRequestDto itemRequestDto) {
        validateRequestData(itemRequestDto);

        return itemRequestClient.addNew(userId, itemRequestDto);
    }

    private void validatePagination(Integer numberFrom, Integer pageSize) {
        if ((pageSize != null && numberFrom != null) && (pageSize < 1 || numberFrom < 0)) {
            throw new RequestControllerBadRequestException("Некорректные параметры запроса с постраничным выводом");

        }

    }

    private void validateRequestData(ItemRequestDto request) {
        if (request.getDescription() == null || request.getDescription().isEmpty()) {
            throw new RequestControllerBadRequestException("Текст заявки не может быть пустым");
        }

    }

}
